function Button({ children, className = "", variant = "primary", ...props }) {
  const variants = {
    primary: "bg-indigo-600 text-white hover:bg-indigo-500 focus:ring-indigo-500",
    secondary: "bg-slate-100 text-slate-700 hover:bg-slate-200 focus:ring-slate-400",
    danger: "bg-rose-50 text-rose-700 hover:bg-rose-100 focus:ring-rose-300",
  };
  return <button className={`inline-flex items-center justify-center rounded-xl px-4 py-2.5 text-sm font-semibold transition focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-60 ${variants[variant]} ${className}`} {...props}>{children}</button>;
}

export default Button;
