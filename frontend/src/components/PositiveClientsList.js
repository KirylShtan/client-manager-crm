import React, { useEffect, useState } from "react";
import { getPositiveClients, deletePositiveClient, updatePositiveClient, searchPositiveClients,
  getPositiveDetails, updatePositiveDetails
 } from "../api/positiveClientService";

const PositiveClientsList = () => {
  const [clients, setClients] = useState([]);
  const [newClient, setNewClient] = useState({ firstName: "", lastName: "", caseNumber: "", status: "", companyName: "" });
  const [selectedPositiveClientId,setSelectedPositiveClientId] = useState(null);
  const [positiveClientDetails,setPositiveClientDetails] = useState(null);
  const [editPositiveNote,setEditPositiveNote] = useState("");
  const [isPositiveEditing,setPositiveIsEditing] = useState(false);

  useEffect(() => {
    getPositiveClients()
      .then(setClients)
      .catch((err) => console.error("Loading error:", err));
  }, []);

  const handleDelete = async (id) => {
    await deletePositiveClient(id);
    setClients(clients.filter((c) => c.id !== id));
  };

  const handleUpdate = async (client) => {
    const firstName = prompt("input name :", client.firstName);
    const lastName = prompt("input lastname:", client.lastName);
    const caseNumber = prompt("input casenumber:", client.caseNumber);
    const status = prompt("input status:", client.status);
    const submissionDate = prompt("input submissiondate (YYYY-MM-DD):", client.submissionDate);
    const companyName = prompt("Input companyName:", client.companyName);

    if (!firstName || !lastName || !caseNumber || !status || !submissionDate || !companyName) {
      alert("All fields are neccesary!");
      return;
    }

    const updatedClient = { ...client, firstName, lastName, caseNumber, status, submissionDate, companyName };

    try {
      const updated = await updatePositiveClient(client.id, updatedClient);
      setClients(clients.map(c => (c.id === client.id ? updated : c)));
    } catch (err) {
      console.error("updating error:", err);
    }
  };

  const [searchTerm, setSearchTerm] = useState("");
  const handleSearch = async () => {
    try {
      const results = await searchPositiveClients({ firstName: searchTerm, lastName: searchTerm, caseNumber: searchTerm, status: searchTerm, companyName: searchTerm });
      setClients(results);
    } catch (err) {
      console.error("Searching error:", err);
    }
  };
  const fetchPositiveClientDetails = async (id) => {
    console.log("Fetching details for id:", id);
    try {
      const details = await getPositiveDetails(id);
      console.log("Raw response from getDetails:", details);
      if (!details || typeof details !== "object") {
        throw new Error("Invalid or empty response from server");
      }
      setPositiveClientDetails(details);
      const note = details.note !== undefined ? details.note : (details.data?.note || "");
      if (note === undefined) {
        console.warn("Note field not found in response:", details);
      }
      setEditPositiveNote(note);
      setSelectedPositiveClientId(id);
    } catch (err) {
      console.error("Error fetching details:", err.message);
      alert(`Failed to load details: ${err.message}. Check console for more info.`);
    }
  };

  const updatePositiveClientNote = async () => {
    console.log("Updating note for id:", selectedPositiveClientId, "with note:", editPositiveNote);
    if (!selectedPositiveClientId) {
      console.error("No client selected for update");
      alert("Please select a client to update.");
      return;
    }
  
    try {
      const response = await updatePositiveDetails(selectedPositiveClientId, editPositiveNote);
      console.log("Update response:", response);
  
      const newPositiveNote = response.note || editPositiveNote; // Используем note из ответа сервера
      setPositiveClientDetails(prev => prev ? { ...prev, note: newPositiveNote } : { id: selectedPositiveClientId, note: newPositiveNote });
      setClients(
        clients.map((c) =>
          c.id === selectedPositiveClientId ? { ...c, note: newPositiveNote } : c
        )
      );
      setPositiveIsEditing(false);
      alert("Note updated successfully!");
    } catch (err) {
      console.error("Error updating note:", err.message);
      alert("Failed to update note: " + err.message);
    }
  };

  return (
    <div style={{ maxWidth: "800px", margin: "20px auto", fontFamily: "Arial, sans-serif" }}>
      <h2 style={{ textAlign: "center", marginBottom: "20px", color: "#2E7D32" }}>
        <div style={{ display: "flex", gap: "10px", marginBottom: "15px" }}>
          <input
            type="text"
            placeholder="Input firstname,lastname,casenumber or company..."
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
              backgroundColor: "#2E7D32",
              color: "white",
              cursor: "pointer",
            }}
          >
            SEARCH
          </button>
        </div>
        Positive Clients
      </h2>

      <table
        style={{
          width: "130%",
          borderCollapse: "collapse",
          boxShadow: "0 4px 10px rgba(0,0,0,0.1)",
          borderRadius: "10px",
          overflow: "hidden",
          marginLeft: "-50px",
          marginRight: "auto",
        }}
      >
        <thead style={{ backgroundColor: "#A5D6A7" }}>
          <tr>
            <th style={thStyle}>ID</th>
            <th style={thStyle}>name</th>
            <th style={thStyle}>lastname</th>
            <th style={thStyle}>casenumber</th>
            <th style={thStyle}>submissiondate</th>
            <th style={thStyle}>status</th>
            <th style={{ ...thStyle, textAlign: "center" }}>operations</th>
            <th style={thStyle}>companyName</th>
          </tr>
        </thead>
        <tbody>
          {clients.map((c) => (
            <tr key={c.id} style={{ backgroundColor: "#f1f8e9" }}>
              <td style={tdStyle}>{c.id}</td>
              <td style={tdStyle}>{c.firstName}</td>
              <td style={tdStyle}>{c.lastName}</td>
              <td style={tdStyle}>{c.caseNumber}</td>
              <td style={tdStyle}>{c.submissionDate}</td>
              <td style={tdStyle}>{c.status}</td>
              <td style={{ ...tdStyle, minWidth: "200px", display: "flex", flexWrap: "nowrap", justifyContent: "flex-start", alignItems: "center", overflow: "hidden", borderBottom: "none" }}>
                <button style={btnUpdate} onClick={() => handleUpdate(c)}>
                  Update
                </button>
                <button style={btnDelete} onClick={() => handleDelete(c.id)}>
                  Delete
                </button>
                <td style={{ textAlign: "right" }}></td>
                <button 
                onClick ={() => fetchPositiveClientDetails(c.id)}
                style={{
                      padding: "6px 12px",
                      borderRadius: "5px",
                      border: "none",
                      backgroundColor: "#9b59b6",
                      color: "white",
                      cursor: "pointer",
                      transition: "all 0.3s",
                      
                      
                      
                    }}
                    onMouseEnter={(e) => {
                      e.target.style.backgroundColor = "#8e44ad";
                      e.target.style.transform = "scale(1.05)";
                    }}
                    onMouseLeave={(e) => {
                      e.target.style.backgroundColor = "#9b59b6";
                      e.target.style.transform = "scale(1)";
                    }}
                    >
                    Show Details  
                    </button> 
              </td>
              <td style={tdStyle}>{c.companyName}</td>
            </tr>
          ))}
        </tbody>
      </table>
      {positiveClientDetails && (
        <div
          style={{
            backgroundColor: "#f5f6fa",
            padding: "15px",
            borderRadius: "5px",
            marginTop: "20px",
            border: "1px solid #ddd",
          }}
        >
          <h3 style={{ marginBottom: "10px", color: "#2ecc71" }}>
            Client Details
          </h3>
          <p>
            <strong>ID:</strong> {positiveClientDetails.id}
          </p>
          <p>
            <strong>Name:</strong> {positiveClientDetails.firstName}
          </p>
          <p>
            <strong>Last Name:</strong> {positiveClientDetails.lastName}
          </p>
          <p>
            <strong>Case Number:</strong> {positiveClientDetails.caseNumber}
          </p>
          
            <strong>PositiveNote:</strong>
            {isPositiveEditing ? (
              <div style={{ marginTop: "5px" }}>
                <input
                  type="text"
                  value={editPositiveNote || ""}
                  onChange={(e) => setEditPositiveNote(e.target.value)}
                  style={{
                    padding: "5px",
                    marginRight: "10px",
                    width: "70%",
                    borderRadius: "5px",
                    border: "1px solid #ccc",
                  }}
                />
                <button
                  onClick={updatePositiveClientNote}
                  style={{
                    padding: "5px 10px",
                    borderRadius: "5px",
                    border: "none",
                    backgroundColor: "#27ae60",
                    color: "white",
                    cursor: "pointer",
                  }}
                >
                  Save
                </button>
                <button
                  onClick={() => setPositiveIsEditing(false)}
                  style={{
                    padding: "5px 10px",
                    borderRadius: "5px",
                    border: "none",
                    backgroundColor: "#e74c3c",
                    color: "white",
                    marginLeft: "5px",
                    cursor: "pointer",
                  }}
                >
                  Cancel
                </button>
              </div>
            ) : (
              <span style={{ marginLeft: "10px" }}>
                {positiveClientDetails.note || "No notes"}
                <button
                  onClick={() => setPositiveIsEditing(true)}
                  style={{
                    padding: "5px 10px",
                    borderRadius: "5px",
                    border: "none",
                    backgroundColor: "#3498db",
                    color: "white",
                    marginLeft: "10px",
                    cursor: "pointer",
                  }}
                >
                  Edit
                </button>
              </span>
            )}
          
        </div>
      )}
    </div>
  );
};

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

export default PositiveClientsList;