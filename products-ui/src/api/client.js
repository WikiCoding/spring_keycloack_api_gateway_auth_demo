import axios from "axios";

const api = axios.create({
  baseURL: process.env.REACT_APP_API_GATEWAY_URL || "http://localhost:8080",
  headers: { "Content-Type": "application/json" },
});

const authConfig = (token) => ({ headers: { Authorization: `Bearer ${token}` } });

export const login = (username, password) => api.post("/api/v1/auth/login", { username, password });
export const getProducts = (token, page) => api.get("/api/v1/products", { ...authConfig(token), params: { page, size: 10 } });
export const createProduct = (token, product) => api.post("/api/v1/products", product, authConfig(token));
export const errorMessage = (error, fallback) => error.response?.data?.detail || error.response?.data?.message || error.response?.data?.error_description || fallback;
