import React, { useState } from "react";
import ActualClientsList from "./components/ActualClientsList";
import PositiveClientsList from "./components/PositiveClientsList";
import NegativeClientsList from "./components/NegativeClientsList";

function App() {
  const [activeTab, setActiveTab] = useState("actual"); // вкладки

  return (
    <div
      style={{
        minHeight: "100vh",
        fontFamily: "Arial, sans-serif",
        color: "#333",
        backgroundImage: "url('https://images.unsplash.com/photo-1503264116251-35a269479413?auto=format&fit=crop&w=1920&q=80')", // 🔥 фон
        backgroundSize: "cover",
        backgroundPosition: "center",
        backgroundAttachment: "fixed",
        padding: "20px",
      }}
    >
      {/* затемняем фон для читаемости */}
      <div
        style={{
          backgroundColor: "rgba(10,10,30,0.85)",
          borderRadius: "15px",
          padding: "20px",
          maxWidth: "1200px",
          margin: "0 auto",
          boxShadow: "0 4px 15px rgba(0,0,0,0.2)",
        }}
      >
        <h1
  style={{
    textAlign: "center",
    marginBottom: "30px",
    fontSize: "3rem",
    fontFamily: "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif",
    fontWeight: "bold",
    background: "linear-gradient(90deg, #4CAF50, #81C784, #388E3C)",
    WebkitBackgroundClip: "text",
    WebkitTextFillColor: "transparent",
    textShadow: "2px 2px 6px rgba(0,0,0,0.3)",
    letterSpacing: "2px",
    animation: "fadeIn 2s ease-in-out",
  }}
  
>
   Clients
</h1>
  

        {/* Кнопки переключения вкладок */}
        <div style={{ textAlign: "center", marginBottom: "20px" }}>
          <button
            onClick={() => setActiveTab("actual")}
            style={{
              marginRight: "10px",
              padding: "8px 15px",
              borderRadius: "5px",
              border: "none",
              backgroundColor: activeTab === "actual" ? "#4CAF50" : "#ccc",
              color: "white",
              cursor: "pointer",
            }}
          >
            Actual
          </button>
          <button
            onClick={() => setActiveTab("positive")}
            style={{
              marginRight: "10px",
              padding: "8px 15px",
              borderRadius: "5px",
              border: "none",
              backgroundColor: activeTab === "positive" ? "#2E7D32" : "#ccc",
              color: "white",
              cursor: "pointer",
            }}
          >
            Positive
          </button>
          <button
            onClick={() => setActiveTab("negative")}
            style={{
              padding: "8px 15px",
              borderRadius: "5px",
              border: "none",
              backgroundColor: activeTab === "negative" ? "#C62828" : "#ccc",
              color: "white",
              cursor: "pointer",
            }}
          >
            Negative
          </button>
        </div>

        
        {activeTab === "actual" && <ActualClientsList />}
        {activeTab === "positive" && <PositiveClientsList />}
        {activeTab === "negative" && <NegativeClientsList />}
      </div>
    </div>
  );
}

export default App;