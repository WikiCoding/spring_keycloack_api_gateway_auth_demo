import { useState } from "react";
import { Link, Navigate } from "react-router-dom";
import { createProduct, errorMessage } from "../api/client";
import { useAuth } from "../auth/AuthContext";
import Button from "../components/Button";

function AddProductPage() {
  const { token } = useAuth();
  const [name, setName] = useState("");
  const [quantity, setQuantity] = useState("");
  const [saved, setSaved] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  if (saved) return <Navigate to="/dashboard" replace />;

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");
    setLoading(true);
    try {
      await createProduct(token, { name, quantity: Number(quantity) });
      setSaved(true);
    } catch (requestError) {
      setError(errorMessage(requestError, "Unable to add the product."));
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="flex min-h-screen items-center justify-center bg-slate-50 px-4 py-10">
      <section className="w-full max-w-lg rounded-3xl bg-white p-8 shadow-soft sm:p-10">
        <p className="text-sm font-bold uppercase tracking-[0.2em] text-indigo-600">Products</p><h1 className="mt-2 text-3xl font-bold text-slate-900">Add product</h1>
        <form onSubmit={handleSubmit} className="mt-8 space-y-5">
          <label className="block text-sm font-semibold text-slate-700">Name<input value={name} onChange={(event) => setName(event.target.value)} required className="mt-2 w-full rounded-xl border border-slate-300 px-3 py-3 text-slate-900 outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100" /></label>
          <label className="block text-sm font-semibold text-slate-700">Quantity<input type="number" min="1" step="1" value={quantity} onChange={(event) => setQuantity(event.target.value)} required className="mt-2 w-full rounded-xl border border-slate-300 px-3 py-3 text-slate-900 outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100" /></label>
          {error && <p role="alert" className="rounded-xl bg-rose-50 p-3 text-sm font-medium text-rose-700">{error}</p>}
          <div className="flex flex-col-reverse gap-3 pt-2 sm:flex-row sm:justify-end"><Link to="/dashboard"><Button type="button" variant="secondary" className="w-full sm:w-auto">Cancel</Button></Link><Button type="submit" disabled={loading} className="w-full sm:w-auto">{loading ? "Adding…" : "Add product"}</Button></div>
        </form>
      </section>
    </main>
  );
}

export default AddProductPage;
