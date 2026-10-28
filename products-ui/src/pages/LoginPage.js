import { useRef, useState } from "react";
import { Navigate } from "react-router-dom";
import { errorMessage, login } from "../api/client";
import { useAuth } from "../auth/AuthContext";
import Button from "../components/Button";
import PasswordInput from "../components/PasswordInput";

function LoginPage() {
  const auth = useAuth();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const usernameInput = useRef(null);

  if (auth.token) return <Navigate to="/dashboard" replace />;

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");
    setLoading(true);
    try {
      const response = await login(username.trim(), password);
      auth.login(response.data.access_token);
    } catch (requestError) {
      setError(errorMessage(requestError, "Unable to sign in. Check your username and password."));
      usernameInput.current?.focus();
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="flex min-h-screen items-center justify-center bg-slate-950 px-4 py-10">
      <section className="w-full max-w-md rounded-3xl bg-white p-8 shadow-soft sm:p-10">
        <p className="text-sm font-bold uppercase tracking-[0.2em] text-indigo-600">Products</p>
        <h1 className="mt-3 text-3xl font-bold tracking-tight text-slate-900">Welcome back</h1>
        <p className="mt-2 text-slate-500">Sign in to manage your product catalogue.</p>
        <form onSubmit={handleSubmit} className="mt-8 space-y-5">
          <label className="block text-sm font-semibold text-slate-700">Username
            <input ref={usernameInput} name="username" value={username} onChange={(event) => setUsername(event.target.value)} autoComplete="username" required className="mt-2 w-full rounded-xl border border-slate-300 px-3 py-3 text-slate-900 outline-none transition focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100" />
          </label>
          <label className="block text-sm font-semibold text-slate-700">Password
            <span className="mt-2 block"><PasswordInput value={password} onChange={(event) => setPassword(event.target.value)} /></span>
          </label>
          {error && <p role="alert" className="rounded-xl bg-rose-50 p-3 text-sm font-medium text-rose-700">{error}</p>}
          <Button type="submit" disabled={loading} className="w-full">{loading ? "Signing in…" : "Sign in"}</Button>
        </form>
      </section>
    </main>
  );
}

export default LoginPage;
