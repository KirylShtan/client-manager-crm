import React, { useState } from "react";

export function Login({ setActiveTab }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = async () => {
    try {
      // Формируем Basic Auth заголовок
      const authHeader = "Basic " + btoa(username + ":" + password);

const response = await fetch("http://localhost:8080/auth/login", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ username, password }),

});

      if (response.ok) {
        // Логин успешный
        localStorage.setItem("loggedIn", "true");
        localStorage.setItem("authHeader", authHeader); // сохраняем для последующих запросов
        setActiveTab("actual");
      } else if (response.status === 401) {
        alert("Неверный логин или пароль");
      } else {
        alert("Ошибка сервера: " + response.status);
      }
    } catch (err) {
      console.error(err);
      alert("Ошибка сети или сервера");
    }
  };

  return (
    <div style={{ maxWidth: "400px", margin: "100px auto", padding: "20px", border: "1px solid #ccc", borderRadius: "8px" }}>
      <h2>Login</h2>
      <input
        type="text"
        placeholder="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        style={{ width: "100%", marginBottom: "10px", padding: "8px" }}
      />
      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        style={{ width: "100%", marginBottom: "10px", padding: "8px" }}
      />
      <button
        onClick={handleLogin}
        style={{ width: "100%", padding: "10px", backgroundColor: "#4CAF50", color: "white", border: "none", borderRadius: "5px" }}
      >
        Login
      </button>
    </div>
  );
}

export default Login;