import React, { useState } from "react";
import ActualClientsList from "./components/ActualClientsList";
import PositiveClientsList from "./components/PositiveClientsList";
import NegativeClientsList from "./components/NegativeClientsList";

function App() {
  const [activeTab, setActiveTab] = useState("actual"); 

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
    <div
        style={{
          borderRadius: "15px",
          padding: "20px",
          maxWidth: "1200px",
          margin: "0 auto",
          
        }}
      >
  <div style={{
  width: "100%",
  backgroundColor: "",
  padding: "10px 20px",
  display: "flex",
  justifyContent: "flex-start",
  alignItems: "center",
  gap: "40px", 
  top: 0,
  left: 0,
  zIndex: 1000
}}>
  <a href="" style={{ color: "#34e321ff", textDecoration: "none", fontWeight: "bold",transition: "all 0.3s",
    animation: "fadeIn 1.5s ease-in-out",fontFamily: "'Poppins', 'Segoe UI', Roboto, sans-serif"
   }}
   onClick={() => setActiveTab("actual")}
   onMouseEnter={(e) =>{
    e.target.style.transform = "scale(1.2)"
    }}
    onMouseLeave={(e) => {
      e.target.style.transform = "scale(1)"
    }}
    >
   
    Actual Clients
  </a>
  <a href="#" style={{ color: "#34e321ff", textDecoration: "none", fontWeight: "bold",transition: "all 0.3s",
    animation: "fadeIn 1.5s ease-in-out",fontFamily: "'Poppins', 'Segoe UI', Roboto, sans-serif"
   }}
    onClick={() => setActiveTab("positive")}
    onMouseEnter={(e) =>{
    e.target.style.transform = "scale(1.2)"
    }}
    onMouseLeave={(e) => {
      e.target.style.transform = "scale(1)"
    }}
    >
    Positive Clients
  </a>
  <a href="#" style={{ color: "#34e321ff", textDecoration: "none", fontWeight: "bold",transition: "all 0.3s",
    animation: "fadeIn 1.5s ease-in-out",fontFamily: "'Poppins', 'Segoe UI', Roboto, sans-serif"
   }}
   onMouseEnter={(e) =>{
    e.target.style.transform = "scale(1.2)"
    }}
    onMouseLeave={(e) => {
      e.target.style.transform = "scale(1)"
    }}
   onClick={() => setActiveTab("negative")}>
    Negative Clients
  </a>
  <a href="https://wyszukiwarka-krs.ms.gov.pl/" target="_blank" rel="noopener norefferer"
  style={{ fontSize: "1.05rem", color: "#34e321ff", textDecoration: "none",transition: "all 0.3s",
    animation: "fadeIn 1.5s ease-in-out",fontFamily: "'Poppins', 'Segoe UI', Roboto, sans-serif",fontWeight: 600
   }}
   onMouseEnter={(e) =>{
    e.target.style.transform = "scale(1.2)"
    }}
    onMouseLeave={(e) => {
      e.target.style.transform = "scale(1)"
    }}>
    KRS
    </a>
  <a href="https://migrant.poznan.uw.gov.pl/" target="_blank" rel="noopener norefferer"
  style ={{fontSize: "1.05rem", color: "#34e321ff", textDecoration: "none",transition: "all 0.3s",
    animation: "fadeIn 1.5s ease-in-out",fontFamily: "'Poppins', 'Segoe UI', Roboto, sans-serif",fontWeight: 600
  }}
  onMouseEnter={(e) =>{
    e.target.style.transform = "scale(1.2)"
    }}
    onMouseLeave={(e) => {
      e.target.style.transform = "scale(1)"
    }}>
    WUW
  </a>
  <a href ="https://aplikacja.ceidg.gov.pl/ceidg/ceidg.public.ui/search.aspx" target ="_blank" rel = "noopener norefferer"
  style ={{fontSize: "1.05rem", color:"#34e321ff", textDecoration: "none",transition: "all 0.3s",
    animation: "fadeIn 1.5s ease-in-out",fontFamily: "'Poppins', 'Segoe UI', Roboto, sans-serif",fontWeight: 600
  }}
  onMouseEnter={(e) =>{
    e.target.style.transform = "scale(1.2)"
    }}
    onMouseLeave={(e) => {
      e.target.style.transform = "scale(1)"
    }}>
    CEIDG
  </a>
  <a href ="https://mos.cudzoziemcy.gov.pl/konto" target ="_blank" rel= "noopener norefferer"
  style = {{fontSize: "1.05rem", color:"#34e321ff", textDecoration: "none",transition: "all 0.3s",
    animation: "fadeIn 1.5s ease-in-out",fontFamily: "'Poppins', 'Segoe UI', Roboto, sans-serif",fontWeight: 600
  }}
  onMouseEnter={(e) =>{
    e.target.style.transform = "scale(1.2)"
    }}
    onMouseLeave={(e) => {
      e.target.style.transform = "scale(1)"
    }}>
    MOS
  </a>
</div>

        
        {activeTab === "actual" && <ActualClientsList />}
        {activeTab === "positive" && <PositiveClientsList />}
        {activeTab === "negative" && <NegativeClientsList />}
      </div>
    </div>
  );
}

export default App;