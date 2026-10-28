import { useState } from "react";

function PasswordInput({ value, onChange }) {
  const [visible, setVisible] = useState(false);
  return (
    <div className="relative">
      <input id="password" name="password" type={visible ? "text" : "password"} value={value} onChange={onChange} autoComplete="current-password" required className="w-full rounded-xl border border-slate-300 bg-white px-3 py-3 pr-20 text-slate-900 outline-none transition focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100" />
      <button type="button" onClick={() => setVisible(!visible)} className="absolute right-2 top-1/2 -translate-y-1/2 rounded-lg px-2 py-1 text-xs font-semibold text-indigo-600 hover:bg-indigo-50">{visible ? "Hide" : "Show"}</button>
    </div>
  );
}

export default PasswordInput;
