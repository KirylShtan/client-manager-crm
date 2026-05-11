import React, { useState } from "react";
import { AUTH_LOGIN_URL } from "../apiConfig";

const Login = ({ onLoginSuccess }) => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = async () => {
    try {
      const response = await fetch(AUTH_LOGIN_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });

      if (!response.ok) {
        alert("Invalid username or password");
        return;
      }

      const data = await response.json();
      const token = data.token;
      localStorage.setItem("authHeader", `Bearer ${token}`);
      onLoginSuccess(`Bearer ${token}`);
    } catch (error) {
      console.error("Login error:", error);
      alert("Connection error");
    }
  };

  return (
    <div
      style={{
        minHeight: "100vh",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        background: "linear-gradient(135deg, #6a11cb, #2575fc)",
        fontFamily: "'Poppins', sans-serif",
      }}
    >
      <div
        style={{
          backgroundColor: "rgba(255, 255, 255, 0.95)",
          padding: "40px 50px",
          borderRadius: "20px",
          boxShadow: "0 15px 40px rgba(0, 0, 0, 0.3)",
          width: "350px",
          textAlign: "center",
          transition: "transform 0.3s ease",
        }}
        onMouseEnter={(e) => (e.currentTarget.style.transform = "scale(1.03)")}
        onMouseLeave={(e) => (e.currentTarget.style.transform = "scale(1)")}
      >
        <h2 style={{ marginBottom: "30px", color: "#333", letterSpacing: "1px" }}>Login</h2>
        
        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          style={{
            width: "100%",
            padding: "12px 15px",
            marginBottom: "20px",
            borderRadius: "10px",
            border: "1px solid #ccc",
            outline: "none",
            transition: "all 0.3s",
          }}
          onFocus={(e) => {
            e.target.style.borderColor = "#2575fc";
            e.target.style.boxShadow = "0 0 8px rgba(37, 117, 252, 0.5)";
          }}
          onBlur={(e) => {
            e.target.style.borderColor = "#ccc";
            e.target.style.boxShadow = "none";
          }}
        />

        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          style={{
            width: "100%",
            padding: "12px 15px",
            marginBottom: "25px",
            borderRadius: "10px",
            border: "1px solid #ccc",
            outline: "none",
            transition: "all 0.3s",
          }}
          onFocus={(e) => {
            e.target.style.borderColor = "#2575fc";
            e.target.style.boxShadow = "0 0 8px rgba(37, 117, 252, 0.5)";
          }}
          onBlur={(e) => {
            e.target.style.borderColor = "#ccc";
            e.target.style.boxShadow = "none";
          }}
        />

        <button
          onClick={handleLogin}
          style={{
            width: "100%",
            padding: "12px",
            borderRadius: "10px",
            border: "none",
            background: "linear-gradient(135deg, #6a11cb, #2575fc)",
            color: "white",
            fontWeight: "bold",
            cursor: "pointer",
            letterSpacing: "1px",
            transition: "all 0.3s",
            boxShadow: "0 5px 15px rgba(0,0,0,0.2)",
          }}
          onMouseEnter={(e) => {
            e.target.style.transform = "scale(1.05)";
            e.target.style.boxShadow = "0 8px 20px rgba(0,0,0,0.3)";
          }}
          onMouseLeave={(e) => {
            e.target.style.transform = "scale(1)";
            e.target.style.boxShadow = "0 5px 15px rgba(0,0,0,0.2)";
          }}
        >
          Login
        </button>
      </div>
    </div>
  );
};

export default Login;