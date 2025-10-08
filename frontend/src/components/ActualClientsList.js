import React, { useEffect, useState } from "react";
import { getActualClients, deleteActualClient, addActualClient, updateActualClient, archiveClient, searchActualClients  } from "../api/clientService";

const ActualClientsList = () => {
  const [clients, setClients] = useState([]);
  const [newClient, setNewClient] = useState({ firstName: "", lastName: "", caseNumber: "", status: "" });

  useEffect(() => {
    getActualClients()
      .then(setClients)
      .catch((err) => console.error("Loading Error:", err));
  }, []);

  const handleDelete = async (id) => {
    await deleteActualClient(id);
    setClients(clients.filter((c) => c.id !== id));
  };

  const handleAdd = async () => {
    if (!newClient.firstName || !newClient.lastName || !newClient.caseNumber || !newClient.status) {
      alert("Complete all fields!");
      return;
    }
    try {
      const clientToSend = {
        ...newClient,
        submissionDate: new Date().toISOString().split("T")[0] 
      };
      const added = await addActualClient(clientToSend);
      setClients([...clients, added]);
      setNewClient({ firstName: "", lastName: "", caseNumber: "", status: "" });
    } catch (err) {
      console.error("Adding error:", err);
    }
  };

  const handleArchive = async (client) => {
  const isPositive = window.confirm("Press OK if case was positively comopleted, press Cancel if not");
  try {
    await archiveClient(client.id, isPositive);
    setClients(clients.filter(c => c.id !== client.id));
    alert(`Cleint ${client.firstName} ${client.lastName} successfully archived в ${isPositive ? "positive" : "negative"} cases`);
  } catch (err) {
    console.error("Archive error:", err);
    alert("Archive error");
  }
};

  const handleUpdate = async (client) => {
    const firstName = prompt("Input firstName:", client.firstName);
    const lastName = prompt("Input sirName:", client.lastName);
    const caseNumber = prompt("Input caseNumber:", client.caseNumber);
    const status = prompt("Input status:", client.status);
    const submissionDate = prompt("Input submissionDate (YYYY-MM-DD):", client.submissionDate);

    if (!firstName || !lastName || !caseNumber || !status || !submissionDate) {
      alert("All fields are necessary!");
      return;
    }

    const updatedClient = { ...client, firstName, lastName, caseNumber, status, submissionDate };

    try {
      const updated = await updateActualClient(client.id, updatedClient);
      setClients(clients.map(c => (c.id === client.id ? updated : c)));
    } catch (err) {
      console.error("Update error:", err);
    }
  };

  const getStatusColor = (status) => {
    switch (status.toLowerCase()) {
      case "processing":
        return "#f1c40f";
      case "completed":
        return "#2ecc71";
      case "failed":
        return "#e74c3c";
      default:
        return "#95a5a6";
    }
  };
  const [searchTerm, setSearchTerm] = useState("");
const [searchResults, setSearchResults] = useState([]);

const handleSearch = async () => {
  try {
    const results = await searchActualClients({ firstName: searchTerm, lastName:searchTerm
      , status:searchTerm, caseNumber:searchTerm});
    setClients(results);
  } catch (err) {
    console.error("Searching error:", err);
  }
};

  return (
    <div style={{ maxWidth: "1000px", margin: "0 auto", fontFamily: "Arial, sans-serif", color: "#333" }}>
     
      <h2 style={{ textAlign: "left", marginBottom: "10px" }}>Adding new client</h2>
      <div style={{ display: "flex", gap: "10px", marginBottom: "20px" }}>
        <input
          type="text"
          placeholder="firstname"
          value={newClient.firstName}
          onChange={(e) => setNewClient({ ...newClient, firstName: e.target.value })}
          style={{ flex: 1, padding: "8px", borderRadius: "5px", border: "1px solid #ccc" }}
        />
        <input
          type="text"
          placeholder="lastname"
          value={newClient.lastName}
          onChange={(e) => setNewClient({ ...newClient, lastName: e.target.value })}
          style={{ flex: 1, padding: "8px", borderRadius: "5px", border: "1px solid #ccc" }}
        />
        <input
          type="text"
          placeholder="casenumber"
          value={newClient.caseNumber}
          onChange={(e) => setNewClient({ ...newClient, caseNumber: e.target.value })}
          style={{ flex: 1, padding: "8px", borderRadius: "5px", border: "1px solid #ccc" }}
        />
        <input
          type="text"
          placeholder="status"
          value={newClient.status}
          onChange={(e) => setNewClient({ ...newClient, status: e.target.value })}
          style={{ flex: 1, padding: "8px", borderRadius: "5px", border: "1px solid #ccc" }}
        />
        
        <button
          onClick={handleAdd}
          style={{
            padding: "8px 15px",
            borderRadius: "5px",
            border: "none",
            backgroundColor: "#4CAF50",
            color: "white",
            cursor: "pointer",
            transition: "all 0.3s",
          }}
          onMouseEnter={(e) => { e.target.style.backgroundColor = "#45a049"; e.target.style.transform = "scale(1.05)"; }}
          onMouseLeave={(e) => { e.target.style.backgroundColor = "#4CAF50"; e.target.style.transform = "scale(1)"; }}
        >
          ADD
        </button>
      </div>
      
<div style={{ display: "flex", gap: "10px", marginBottom: "15px" }}>
  <input
    type="text"
    placeholder="Input firstname,lastname or casenumber..."
    value={searchTerm}
    onChange={(e) => setSearchTerm(e.target.value)}
    style={{ flex: 1, padding: "8px", borderRadius: "5px", border: "1px solid #ccc" }}
  />
  <button
    onClick={handleSearch}
    style={{
      padding: "8px 15px",
      borderRadius: "5px",
      border: "none",
      backgroundColor: "#4CAF50",
      color: "white",
      cursor: "pointer",
    }}
  >
    SEARCH
  </button>
</div>
{searchResults.length > 0 && (
        <div style={{ marginBottom: "20px" }}>
          <h3>Searching result</h3>
          <table style={tableStyle}>
            <thead>
              <tr style={theadStyle}>
                <th style={thStyle}>ID</th>
                <th style={thStyle}>name</th>
                <th style={thStyle}>lastname</th>
                <th style={thStyle}>casenumber</th>
                <th style={thStyle}>submissiondate</th>
                <th style={thStyle}>status</th>
                <th style={thStyle}>operations</th>
              </tr>
            </thead>
            <tbody>
              {searchResults.map((c) => (
                <tr key={c.id} style={trStyle}>
                  <td style={tdStyle}>{c.id}</td>
                  <td style={tdStyle}>{c.firstName}</td>
                  <td style={tdStyle}>{c.lastName}</td>
                  <td style={tdStyle}>{c.caseNumber}</td>
                  <td style={tdStyle}>{c.submissionDate}</td>
                  <td style={tdStyle}>{c.status}</td>
                  <td style={tdStyle}>
                    <button style={btnUpdate} onClick={() => handleUpdate(c)}>Обновить</button>
                    <button style={btnDelete} onClick={() => handleDelete(c.id)}>Удалить</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <h2 style={{ textAlign: "left", marginBottom: "10px" }}>List of actual clients</h2>
      <div style={{ overflowX: "auto" }}>
        
        <table style={{
          width: "100%",
          borderCollapse: "separate",
          borderSpacing: "0 5px",
          borderRadius: "10px",
          overflow: "hidden",
          boxShadow: "0 2px 10px rgba(0,0,0,0.1)",
          animation: "fadeIn 0.5s ease-in-out",
        }}>
          <thead>
            <tr style={{ backgroundColor: "#4CAF50", color: "white", textAlign: "left" }}>
              <th style={{ padding: "10px" }}>ID</th>
              <th style={{ padding: "10px" }}>name</th>
              <th style={{ padding: "10px" }}>lastname</th>
              <th style={{ padding: "10px" }}>casenumber</th>
              <th style={{ padding: "10px" }}>submissiondate</th>
              <th style={{ padding: "10px" }}>status</th>
              <th style={{ padding: "10px" }}>operations</th>
            </tr>
          </thead>
          <tbody>
            {clients.map((c, i) => (
             
              <tr
                key={c.id}
                style={{
                  backgroundColor: i % 2 === 0 ? "#f9f9f9" : "white",
                  transition: "background-color 0.3s, transform 0.2s",
                }}
                onMouseEnter={(e) => { e.currentTarget.style.backgroundColor = "#dff0d8"; e.currentTarget.style.transform = "scale(1.01)"; }}
                onMouseLeave={(e) => { e.currentTarget.style.backgroundColor = i % 2 === 0 ? "#f9f9f9" : "white"; e.currentTarget.style.transform = "scale(1)"; }}
              >
                <td style={{ padding: "10px" }}>{c.id}</td>
                <td style={{ padding: "10px" }}>{c.firstName}</td>
                <td style={{ padding: "10px" }}>{c.lastName}</td>
                <td style={{ padding: "10px" }}>{c.caseNumber}</td>
                <td style={{ padding: "10px" }}>{c.submissionDate}</td>
                <td style={{ padding: "10px", fontWeight: "bold", color: getStatusColor(c.status) }}>{c.status}</td>
                <td style={{ padding: "10px" }}>
                  
                  <button
                    onClick={() => handleDelete(c.id)}
                    style={{
                      padding: "5px 10px",
                      borderRadius: "5px",
                      border: "none",
                      backgroundColor: "#e74c3c",
                      color: "white",
                      cursor: "pointer",
                      transition: "all 0.3s",
                    }}
                    onMouseEnter={(e) => { e.target.style.backgroundColor = "#c0392b"; e.target.style.transform = "scale(1.05)"; }}
                    onMouseLeave={(e) => { e.target.style.backgroundColor = "#e74c3c"; e.target.style.transform = "scale(1)"; }}
                  >
                    Delete
                  </button>
                  <button
                    onClick={() => handleUpdate(c)}
                    style={{
                      padding: "5px 10px",
                      borderRadius: "5px",
                      border: "none",
                      backgroundColor: "#3498db",
                      color: "white",
                      cursor: "pointer",
                      marginLeft: "5px",
                      transition: "all 0.3s",
                    }}
                    onMouseEnter={(e) => { e.target.style.backgroundColor = "#2980b9"; e.target.style.transform = "scale(1.05)"; }}
                    onMouseLeave={(e) => { e.target.style.backgroundColor = "#3498db"; e.target.style.transform = "scale(1)"; }}
                  >
                    Update
                  </button>
                  <button
                    onClick={() => handleArchive(c)}
                    style={{
                      padding: "5px 10px",
                      borderRadius: "5px",
                      border: "none",
                      backgroundColor: "#8e44ad",
                      color: "white",
                      cursor: "pointer",
                      marginLeft: "5px",
                      transition: "all 0.3s",
                    }}
                    onMouseEnter={(e) => { e.target.style.backgroundColor = "#732d91"; e.target.style.transform = "scale(1.05)"; }}
                    onMouseLeave={(e) => { e.target.style.backgroundColor = "#8e44ad"; e.target.style.transform = "scale(1)"; }}
                  >
                    Archive
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
const tableStyle = {
  width: "100%",
  borderCollapse: "collapse",
  borderRadius: "10px",
  overflow: "hidden",
  boxShadow: "0 4px 10px rgba(0,0,0,0.1)",
};
const theadStyle = { backgroundColor: "#4CAF50", color: "white" };
const thStyle = { padding: "12px", textAlign: "left", borderBottom: "2px solid #ddd" };
const tdStyle = { padding: "10px", borderBottom: "1px solid #ddd" };
const trStyle = { backgroundColor: "#f9f9f9" };
const btnUpdate = {
  marginRight: "10px",
  padding: "6px 12px",
  borderRadius: "5px",
  border: "none",
  backgroundColor: "#42A5F5",
  color: "white",
  cursor: "pointer",
};
const btnDelete = {
  padding: "6px 12px",
  borderRadius: "5px",
  border: "none",
  backgroundColor: "#E53935",
  color: "white",
  cursor: "pointer",
};

export default ActualClientsList;