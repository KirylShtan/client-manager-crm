import React, { useState } from "react";

const Login = ({ onLoginSuccess }) => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = async () => {
    try {
      const response = await fetch("http://localhost:8080/auth/login", {
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
    <div style={{ textAlign: "center", marginTop: "100px", color: "white" }}>
      <h2>Login</h2>
      <input
        type="text"
        placeholder="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        style={{ marginBottom: "10px", padding: "5px", color: "black", backgroundColor: "white" }}
      />
      <br />
      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        style={{ marginBottom: "10px", padding: "5px", color: "black", backgroundColor: "white" }}
      />
      <br />
      <button
        onClick={handleLogin}
        style={{
          padding: "5px 10px",
          borderRadius: "5px",
          border: "none",
          backgroundColor: "#27ae60",
          color: "white",
          cursor: "pointer",
        }}
      >
        Login
      </button>
    </div>
  );
};

export default Login;