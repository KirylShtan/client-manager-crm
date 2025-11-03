import React, { useState } from "react";
import ActualClientsList from "./components/ActualClientsList";
import PositiveClientsList from "./components/PositiveClientsList";
import NegativeClientsList from "./components/NegativeClientsList";
import Login from "./components/Login";

function App() {
  const [activeTab, setActiveTab] = useState("actual");
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem("authHeader"));

  const handleLoginSuccess = (authHeader) => {
    localStorage.setItem("authHeader", authHeader);
    localStorage.setItem("loggedIn", "true");
    setIsLoggedIn(true);
  };

  const handleLogout = () => {
    localStorage.removeItem("authHeader");
    localStorage.removeItem("loggedIn");
    setIsLoggedIn(false);
  };

  if (!isLoggedIn) {
    return <Login onLoginSuccess={handleLoginSuccess} />;
  }

  return (
    <div
      style={{
        minHeight: "100vh",
        fontFamily: "Arial, sans-serif",
        color: "#34e321",
        backgroundColor: "black",
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
        {/* Панель навигации */}
        <div
          style={{
            width: "100%",
            padding: "10px 20px",
            display: "flex",
            justifyContent: "flex-start",
            alignItems: "center",
            gap: "40px",
            zIndex: 1000,
          }}
        >
          {[
            { label: "Actual Clients", tab: "actual" },
            { label: "Positive Clients", tab: "positive" },
            { label: "Negative Clients", tab: "negative" },
          ].map(({ label, tab }) => (
            <a
              key={tab}
              href="#"
              style={{
                color: activeTab === tab ? "#50fa7b" : "#34e321ff",
                textDecoration: "none",
                fontWeight: "bold",
                transition: "all 0.3s",
                animation: "fadeIn 1.5s ease-in-out",
                fontFamily: "'Poppins', 'Segoe UI', Roboto, sans-serif",
              }}
              onClick={() => setActiveTab(tab)}
              onMouseEnter={(e) => (e.target.style.transform = "scale(1.2)")}
              onMouseLeave={(e) => (e.target.style.transform = "scale(1)")}
            >
              {label}
            </a>
          ))}

          {/* Ссылки */}
          {[
            { label: "KRS", url: "https://wyszukiwarka-krs.ms.gov.pl/" },
            { label: "WUW", url: "https://migrant.poznan.uw.gov.pl/" },
            { label: "CEIDG", url: "https://aplikacja.ceidg.gov.pl/ceidg/ceidg.public.ui/search.aspx" },
            { label: "MOS", url: "https://mos.cudzoziemcy.gov.pl/konto" },
          ].map(({ label, url }) => (
            <a
              key={url}
              href={url}
              target="_blank"
              rel="noopener noreferrer"
              style={{
                fontSize: "1.05rem",
                color: "#34e321ff",
                textDecoration: "none",
                transition: "all 0.3s",
                animation: "fadeIn 1.5s ease-in-out",
                fontFamily: "'Poppins', 'Segoe UI', Roboto, sans-serif",
                fontWeight: 600,
              }}
              onMouseEnter={(e) => (e.target.style.transform = "scale(1.2)")}
              onMouseLeave={(e) => (e.target.style.transform = "scale(1)")}
            >
              {label}
            </a>
          ))}

          {/* Logout */}
          <button
            onClick={handleLogout}
            style={{
              marginLeft: "auto",
              backgroundColor: "transparent",
              border: "1px solid #34e321ff",
              borderRadius: "8px",
              color: "#34e321ff",
              padding: "5px 10px",
              cursor: "pointer",
              fontFamily: "'Poppins', sans-serif",
              transition: "all 0.3s",
            }}
            onMouseEnter={(e) => (e.target.style.backgroundColor = "#34e32133")}
            onMouseLeave={(e) => (e.target.style.backgroundColor = "transparent")}
          >
            Logout
          </button>
        </div>

        {/* Содержимое вкладок */}
        {activeTab === "actual" && <ActualClientsList />}
        {activeTab === "positive" && <PositiveClientsList />}
        {activeTab === "negative" && <NegativeClientsList />}
      </div>
    </div>
  );
}

export default App;