import React, { useState } from "react";

const Login = ({ onLoginSuccess }) => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = async () => {
    const authHeader = "Basic " + btoa(username + ":" + password);

    try {
      
      const response = await fetch("http://localhost:8080/api/ActualClients/actual", {
        method: "GET",
        headers: {
          Authorization: authHeader,
        },
      });

      if (response.ok) {
        onLoginSuccess(authHeader);
      } else if (response.status === 401) {
        alert("Invalid entry data");
      } else {
        alert("Acess denied , check configuration");
      }
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
        style={{ marginBottom: "10px", padding: "5px", color: "black",backgroundColor: "white" }}
      />
      <br />
      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        style={{ marginBottom: "10px", padding: "5px",color: "black",backgroundColor: "white" }}
      />
      <br />
      <button onClick={handleLogin} style={{ padding: "5px 10px",
                    borderRadius: "5px",
                    border: "none",
                    backgroundColor: "#27ae60",
                    color: "white",
                    cursor: "pointer", }}>
        Login
      </button>
    </div>
  );
};

export default Login;