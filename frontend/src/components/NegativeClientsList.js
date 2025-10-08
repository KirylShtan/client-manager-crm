import React, { useEffect, useState } from "react";
import {getNegativeClients , deleteNegativeClient, updateNegativeClient, searchNegativeClients} from "../api/negativeClientService"; 

const NegativeClientsList = () => {
  const [clients, setClients] = useState([]);
  const [newClient, setNewClient] = useState({ firstName: "", lastName: "", caseNumber: "", status: "" });

useEffect(() => {
    getNegativeClients()
      .then(setClients)
      .catch((err) => console.error("Loading error:", err));
  }, []);

   const handleDelete = async (id) => {
       await deleteNegativeClient(id);
       setClients(clients.filter((c) => c.id !== id));
     };

     const handleUpdate = async (client) => {
         const firstName = prompt("input name :", client.firstName);
         const lastName = prompt("input lastname:", client.lastName);
         const caseNumber = prompt("input casenumber:", client.caseNumber);
         const status = prompt("input status:", client.status);
         const submissionDate = prompt("input submissiondate (YYYY-MM-DD):", client.submissionDate);
     
         if (!firstName || !lastName || !caseNumber || !status || !submissionDate) {
           alert("All fields are neccesary!");
           return;
         }
     
         const updatedClient = { ...client, firstName, lastName, caseNumber, status, submissionDate };
     
         try {
           const updated = await updateNegativeClient(client.id, updatedClient);
           setClients(clients.map(c => (c.id === client.id ? updated : c)));
         } catch (err) {
           console.error("updating error:", err);
         }
       };
       
       
       const [searchTerm, setSearchTerm] = useState(""); 
       const handleSearch = async () => {
    try {
      const results = await searchNegativeClients({ firstName: searchTerm, lastName: searchTerm, caseNumber: searchTerm, status:searchTerm });
      setClients(results);
    } catch (err) {
      console.error("Searching error:", err);
    }
  };


       return (
    <div style={{ maxWidth: "800px", margin: "20px auto", fontFamily: "Arial, sans-serif" }}>
      <h2 style={{ textAlign: "center", marginBottom: "20px", color: "#C62828" }}>

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
            backgroundColor: "#C62828",
            color: "white",
            cursor: "pointer",
          }}
        >
          SEARCH
        </button>
      </div>
        Negative Clients
      </h2>
      
      <table
        style={{
          width: "100%",
          borderCollapse: "collapse",
          boxShadow: "0 4px 10px rgba(0,0,0,0.1)",
          borderRadius: "10px",
          overflow: "hidden",
        }}
      >
        <thead style={{ backgroundColor: "#EF9A9A" }}>
          <tr>
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
          {clients.map((c) => (
            <tr key={c.id} style={{ backgroundColor: "#fff9f9" }}>
              <td style={tdStyle}>{c.id}</td>
              <td style={tdStyle}>{c.firstName}</td>
              <td style={tdStyle}>{c.lastName}</td>
              <td style={tdStyle}>{c.caseNumber}</td>
              <td style={tdStyle}>{c.submissionDate}</td>
              <td style={tdStyle}>{c.status}</td>
              <td style={tdStyle}>
                <button style={btnUpdate} onClick={() => handleUpdate(c)}>
                  Update
                </button>
                <button style={btnDelete} onClick={() => handleDelete(c.id)}>
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
  

}
const thStyle = {
  padding: "12px",
  textAlign: "left",
  fontWeight: "bold",
  borderBottom: "2px solid #ddd",
};

const tdStyle = {
  padding: "10px",
  borderBottom: "1px solid #ddd",
};

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
export default NegativeClientsList;