import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { errorMessage, getProducts } from "../api/client";
import { useAuth } from "../auth/AuthContext";
import Button from "../components/Button";
import ProductCard from "../components/ProductCard";

function DashboardPage() {
  const { token, logout } = useAuth();
  const [products, setProducts] = useState([]);
  const [page, setPage] = useState(0);
  const [pageData, setPageData] = useState({ hasNext: false });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;
    setLoading(true);
    setError("");
    getProducts(token, page)
      .then((response) => {
        if (!active) return;
        setProducts(response.data.items);
        setPageData(response.data);
      })
      .catch((requestError) => {
        if (!active) return;
        if (requestError.response?.status === 401) logout();
        else setError(errorMessage(requestError, "Unable to load products."));
      })
      .finally(() => active && setLoading(false));
    return () => { active = false; };
  }, [page, token, logout]);

  return (
    <main className="min-h-screen bg-slate-50 px-4 py-8 sm:px-8">
      <div className="mx-auto max-w-6xl">
        <header className="flex flex-col gap-4 border-b border-slate-200 pb-7 sm:flex-row sm:items-center sm:justify-between">
          <div><p className="text-sm font-bold uppercase tracking-[0.2em] text-indigo-600">Products</p><h1 className="mt-2 text-3xl font-bold text-slate-900">Dashboard</h1></div>
          <div className="flex gap-3"><Link to="/products/new"><Button>Add product</Button></Link><Button variant="danger" onClick={logout}>Log out</Button></div>
        </header>
        <section className="py-8">
          <div className="mb-5 flex items-center justify-between"><h2 className="text-lg font-semibold text-slate-800">All products</h2></div>
          {error && <p role="alert" className="mb-5 rounded-xl bg-rose-50 p-4 text-sm font-medium text-rose-700">{error}</p>}
          {loading ? <p className="py-12 text-center text-slate-500">Loading products…</p> : products.length === 0 ? <div className="rounded-2xl border border-dashed border-slate-300 bg-white p-12 text-center text-slate-500">No products yet. Add your first one.</div> : <div className="space-y-4">{products.map((product) => <ProductCard key={product.id} product={product} />)}</div>}
        </section>
        <footer className="flex items-center justify-between border-t border-slate-200 pt-5 text-sm text-slate-600">
          <span>Page {page + 1}</span>
          <div className="flex gap-2"><Button variant="secondary" disabled={page === 0 || loading} onClick={() => setPage(page - 1)}>Previous</Button><Button variant="secondary" disabled={loading || !!error || !pageData.hasNext} onClick={() => setPage(page + 1)}>Next</Button></div>
        </footer>
      </div>
    </main>
  );
}

export default DashboardPage;
