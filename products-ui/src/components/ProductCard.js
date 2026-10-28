function ProductCard({ product }) {
  return (
    <article className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm transition hover:-translate-y-0.5 hover:shadow-md">
      <p className="break-all font-mono text-xs text-slate-400">{product.id}</p>
      <h2 className="mt-3 text-lg font-bold capitalize text-slate-900">{product.name}</h2>
      <div className="mt-4 inline-flex rounded-lg bg-indigo-50 px-3 py-1 text-sm font-semibold text-indigo-700">Quantity: {product.quantity}</div>
    </article>
  );
}

export default ProductCard;
