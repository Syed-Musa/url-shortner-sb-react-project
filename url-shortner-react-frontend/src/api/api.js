import axios from "axios";

console.log("BASE URL:", import.meta.env.VITE_BACKEND_URL);

export default axios.create({
    baseURL: import.meta.env.VITE_BACKEND_URL,
});