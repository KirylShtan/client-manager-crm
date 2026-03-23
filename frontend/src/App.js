import React, { useState } from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import ActualClientsList from "./components/ActualClientsList";
import CompletedClientsList from "./components/CompletedClienstList";
import Login from "./components/Login";
import ClientFilesPage from "./components/ClientFilesPage"; 
import { motion } from "framer-motion";

function App() {
  const [activeTab, setActiveTab] = useState("actual");
  const [isLoggedIn, setIsLoggedIn] = useState(false);

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

  if (!isLoggedIn) return <Login onLoginSuccess={handleLoginSuccess} />;

  return (
    <Router>
      <Routes>
        <Route
          path="/"
          element={
            <div className="min-h-screen bg-gradient-to-r from-purple-700 via-pink-600 to-red-600 p-6">
              <div className="w-full h-full bg-black/50 p-6 rounded-2xl shadow-2xl backdrop-blur-sm">
                
                
                <div className="flex flex-wrap items-center gap-6 mb-6">
                  {[
                    { label: "Actual Clients", tab: "actual" },
                    { label: "Completed Cases", tab: "completed" },
                  ].map(({ label, tab }) => (
                    <motion.a
                      key={tab}
                      href="#"
                      onClick={() => setActiveTab(tab)}
                      whileHover={{ scale: 1.2 }}
                      className={`font-bold text-lg transition-all duration-300 ${
                        activeTab === tab ? "text-green-400" : "text-green-200"
                      }`}
                    >
                      {label}
                    </motion.a>
                  ))}

                  
                  {[
                    { label: "KRS", url: "https://wyszukiwarka-krs.ms.gov.pl/" },
                    { label: "WUW", url: "https://migrant.poznan.uw.gov.pl/" },
                    { label: "CEIDG", url: "https://aplikacja.ceidg.gov.pl/ceidg/ceidg.public.ui/search.aspx" },
                    { label: "MOS", url: "https://mos.cudzoziemcy.gov.pl/konto" },
                  ].map(({ label, url }) => (
                    <motion.a
                      key={url}
                      href={url}
                      target="_blank"
                      rel="noopener noreferrer"
                      whileHover={{ scale: 1.1 }}
                      className="text-green-300 font-medium hover:text-green-100 transition-colors"
                    >
                      {label}
                    </motion.a>
                  ))}

                  
                  <motion.button
                    onClick={handleLogout}
                    whileHover={{ scale: 1.05, backgroundColor: "#34e32155" }}
                    className="ml-auto border border-green-400 text-green-400 px-4 py-1 rounded-lg transition-all"
                  >
                    Logout
                  </motion.button>
                </div>

                
                <motion.div
                  key={activeTab}
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ duration: 0.4 }}
                  className="bg-black/50 p-4 rounded-xl shadow-lg backdrop-blur-sm"
                >
                  {activeTab === "actual" && <ActualClientsList authHeader={localStorage.getItem("authHeader")} />}
                  {activeTab === "completed" && <CompletedClientsList />}
                </motion.div>
              </div>
            </div>
          }
        />
        <Route path="/client/:clientUuid/files" element={<ClientFilesPage />} />
      </Routes>
    </Router>
  );
}

export default App;