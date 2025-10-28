import React, { useEffect, useState } from "react";
import {
  getActualClients,
  deleteActualClient,
  addActualClient,
  updateActualClient,
  archiveClient,
  searchActualClients,
  getDetails,
  updateDetails,
} from "../api/clientService";

const ActualClientsList = () => {
  const [clients, setClients] = useState([]);
  const [newClient, setNewClient] = useState({
    firstName: "",
    lastName: "",
    caseNumber: "",
    status: "",
    companyName: "",
    
  });
  const [selectedClientId, setSelectedClientId] = useState(null); 
  const [clientDetails, setClientDetails] = useState(null); 
  const [editNote, setEditNote] = useState(""); 
  const [isEditing, setIsEditing] = useState(false); 
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    getActualClients()
      .then(setClients)
      .catch((err) => console.error("Loading Error:", err));
  }, []);

  const handleDelete = async (id) => {
    await deleteActualClient(id);
    setClients(clients.filter((c) => c.id !== id));
    if (selectedClientId === id) {
      setClientDetails(null); 
      setSelectedClientId(null);
    }
  };

  const handleAdd = async () => {
    if (
      !newClient.firstName ||
      !newClient.lastName ||
      !newClient.caseNumber ||
      !newClient.status ||
      !newClient.companyName
    ) {
      alert("Complete all fields!");
      return;
    }
    try {
      const clientToSend = {
        ...newClient,
        submissionDate: new Date().toISOString().split("T")[0],
      };
      const added = await addActualClient(clientToSend);
      setClients([...clients, added]);
      setNewClient({
        firstName: "",
        lastName: "",
        caseNumber: "",
        status: "",
        companyName: "",
      });
    } catch (err) {
      console.error("Adding error:", err);
    }
  };

  const handleArchive = async (client) => {
    const isPositive = window.confirm(
      "Press OK if case was positively completed, press Cancel if not"
    );
    try {
      await archiveClient(client.id, isPositive);
      setClients(clients.filter((c) => c.id !== client.id));
      alert(
        `Client ${client.firstName} ${client.lastName} successfully archived in ${
          isPositive ? "positive" : "negative"
        } cases`
      );
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
    const submissionDate = prompt(
      "Input submissionDate (YYYY-MM-DD):",
      client.submissionDate
    );
    const companyName = prompt("Input companyName:", client.companyName);
    const payed = prompt("Input payment status:", client.payed);

    if (
      !firstName ||
      !lastName ||
      !caseNumber ||
      !status ||
      !submissionDate ||
      !companyName ||
      !payed
    ) {
      alert("All fields are necessary!");
      return;
    }

    const updatedClient = {
      ...client,
      firstName,
      lastName,
      caseNumber,
      status,
      submissionDate,
      companyName,
      payed
    };

    try {
      const updated = await updateActualClient(client.id, updatedClient);
      setClients(clients.map((c) => (c.id === client.id ? updated : c)));
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
      
      const results = await searchActualClients({
        firstName: searchTerm,
        lastName: searchTerm,
        status: searchTerm,
        caseNumber: searchTerm,
        companyName: searchTerm,
        submissionDate: searchTerm
        
      });
      setClients(results);
    } catch (err) {
      console.error("Searching error:", err);
      alert("Invalid data pattern, expected yyyy-MM-dd");
    }
  };
const fetchClientDetails = async (id) => {
  console.log("Fetching details for id:", id);
  try {
    const details = await getDetails(id);
    console.log("Raw response from getDetails:", details);
    if (!details || typeof details !== "object") {
      throw new Error("Invalid or empty response from server");
    }
    setClientDetails(details);
    const note = details.note !== undefined ? details.note : (details.data?.note || "");
    if (note === undefined) {
      console.warn("Note field not found in response:", details);
    }
    setEditNote(note);
    setSelectedClientId(id);
  } catch (err) {
    console.error("Error fetching details:", err.message);
    alert(`Failed to load details: ${err.message}. Check console for more info.`);
  }
};

  const updateClientNote = async () => {
  console.log("Updating note for id:", selectedClientId, "with note:", editNote);
  if (!selectedClientId) {
    console.error("No client selected for update");
    alert("Please select a client to update.");
    return;
  }

  try {
    const response = await updateDetails(selectedClientId, editNote);
    console.log("Update response:", response);

   
    let updatedData;
    if (response.data && typeof response.data === "object") {
      updatedData = response.data;
    } else if (response.note !== undefined) {
      updatedData = response; 
    } else {
      updatedData = { note: editNote }; 
      console.warn("Unexpected response format, using editNote as fallback:", response);
    }

    const newNote = updatedData.note || editNote; 
    setClientDetails({ ...clientDetails, note: newNote }); 
    setClients(
      clients.map((c) =>
        c.id === selectedClientId ? { ...c, note: newNote } : c 
      )
    );
    setIsEditing(false);
    alert("Note updated successfully!");
  } catch (err) {
    console.error("Error updating note:", err.message);
    alert("Failed to update note: " + err.message);
  }
};
const fadeIn = `
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
`;

const styleSheet = document.createElement("style");
styleSheet.type = "text/css";
styleSheet.innerText = fadeIn;
document.head.appendChild(styleSheet);

  return (
    <div
      style={{
        maxWidth: "1000px",
        marginLeft: "-4px",
        fontFamily: "Arial, sans-serif",
        color: "#333",
        borderRadius: "15px",
      }}
    >
      <h2
        style={{
        textAlign: "center",
        marginBottom: "20px",
        color: "#34e321ff",
        marginLeft: "160px",
        fontSize: "28px",            
        fontWeight: "800",           
        letterSpacing: "1.5px",     
        textShadow: "2px 2px 6px rgba(0,0,0,0.2)", 
        textTransform: "uppercase", 
        animation: "fadeIn 1.5s ease-in-out",
      
  }}
>
  ADDING NEW CLIENT
</h2>
      <div
        style={{ display: "flex", gap: "10px", marginBottom: "20px", width: "118%" }}
      >
        <input
          type="text"
          placeholder="firstname"
          value={newClient.firstName}
          onChange={(e) =>
            setNewClient({ ...newClient, firstName: e.target.value })
          }
          style={{ flex: 1, padding: "8px", borderRadius: "5px", border: "1px solid #ccc" }}
        />
        <input
          type="text"
          placeholder="lastname"
          value={newClient.lastName}
          onChange={(e) =>
            setNewClient({ ...newClient, lastName: e.target.value })
          }
          style={{ flex: 1, padding: "8px", borderRadius: "5px", border: "1px solid #ccc" }}
        />
        <input
          type="text"
          placeholder="casenumber"
          value={newClient.caseNumber}
          onChange={(e) =>
            setNewClient({ ...newClient, caseNumber: e.target.value })
          }
          style={{ flex: 1, padding: "8px", borderRadius: "5px", border: "1px solid #ccc" }}
        />
        <input
          type="text"
          placeholder="status"
          value={newClient.status}
          onChange={(e) =>
            setNewClient({ ...newClient, status: e.target.value })
          }
          style={{ flex: 1, padding: "8px", borderRadius: "5px", border: "1px solid #ccc" }}
        />
        <input
          type="text"
          placeholder="companyName"
          value={newClient.companyName}
          onChange={(e) =>
            setNewClient({ ...newClient, companyName: e.target.value })
          }
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
          onMouseEnter={(e) => {
            e.target.style.backgroundColor = "#45a049";
            e.target.style.transform = "scale(1.05)";
          }}
          onMouseLeave={(e) => {
            e.target.style.backgroundColor = "#4CAF50";
            e.target.style.transform = "scale(1)";
          }}
        >
          ADD
        </button>
      </div>

      <div
        style={{ display: "flex", gap: "10px", marginBottom: "15px", width: "118%" }}
      >
        <input
          type="text"
          placeholder="Input firstname, lastname or casenumber..."
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
          onMouseEnter={(e) => {
            e.target.style.backgroundColor = "#45a049";
            e.target.style.transform = "scale(1.05)";
          }}
          onMouseLeave={(e) => {
            e.target.style.backgroundColor = "#4CAF50";
            e.target.style.transform = "scale(1)";
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
                <th style={thStyle}>companyName</th>
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
                  <td style={tdStyle}>{c.companyName}</td>
                  <td style={{ ...tdStyle, display: "flex", gap: "5px" }}>
                    <button 
                      style={btnUpdate}
                      onClick={() => handleUpdate(c)}
                    >
                      Обновить
                    </button>
                    <button
                      style={btnDelete}
                      onClick={() => handleDelete(c.id)}
                    >
                      Удалить
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <h2 style={{ textAlign: "center",
        marginBottom: "20px",
        color: "#34e321ff",
        marginLeft: "160px",
        fontSize: "28px",            
        fontWeight: "800",           
        letterSpacing: "1.5px",     
        textShadow: "2px 2px 6px rgba(0,0,0,0.2)", 
        textTransform: "uppercase", 
        animation: "fadeIn 1.5s ease-in-out", }}>
        List of actual clients
      </h2>
      <div
        style={{
          overflowX: "auto",
          overflow: "hidden",
          borderRadius: "10px",
          width: "118%",
          fontFamily: "'Poppins', 'Segoe UI', Roboto, sans-serif",
          fontSize: "15px",                                        
          letterSpacing: "0.3px",                                 
        }}
      >
        <table
          style={{
            width: "100%",
            borderCollapse: "separate",
            borderSpacing: "0",
            borderRadius: "10px",
            overflow: "hidden",
            boxShadow: "0 2px 10px rgba(0,0,0,0.1)",
            animation: "fadeIn 0.5s ease-in-out",
          }}
        >
          <thead>
            <tr
              style={{ backgroundColor: "#4CAF50", color: "white", textAlign: "center" }}
            >
              <th style={{ padding: "10px" }}>ID</th>
              <th style={{ padding: "10px" }}>name</th>
              <th style={{ padding: "10px" }}>lastname</th>
              <th style={{ padding: "10px" }}>casenumber</th>
              <th style={{ padding: "10px" }}>submissiondate</th>
              <th style={{ padding: "10px" }}>status</th>
              <th style={{ padding: "10px" }}>operations</th>
              <th style={{ padding: "10px", marginRight: "10px" }}>companyName</th>
              <th style={{ padding: "10px" }}>payed</th>
              
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
                onMouseEnter={(e) => {
                  e.currentTarget.style.backgroundColor = "#dff0d8";
                  e.currentTarget.style.transform = "scale(1.01)";
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.backgroundColor =
                    i % 2 === 0 ? "#f9f9f9" : "white";
                  e.currentTarget.style.transform = "scale(1)";
                }}
              >
                <td style={{ padding: "10px" }}>{c.id}</td>
                <td style={{ padding: "10px" }}>{c.firstName}</td>
                <td style={{ padding: "10px" }}>{c.lastName}</td>
                <td style={{ padding: "10px" }}>{c.caseNumber}</td>
                <td style={{ padding: "10px" }}>{c.submissionDate}</td>
                <td
                  style={{
                    padding: "10px",
                    fontWeight: "bold",
                    color: getStatusColor(c.status),
                  }}
                >
                  {c.status}
                </td>
                <td
                  style={{
                    padding: "10px",
                    display: "flex",
                    gap: "5px",
                    borderBottom: "1px solid #ddd",
                  }}
                >
                  <button
                    onClick={() => handleDelete(c.id)}
                    style={{
                      padding: "8px",
                      borderRadius: "5px",
                      border: "none",
                      backgroundColor: "#e74c3c",
                      color: "white",
                      cursor: "pointer",
                      transition: "all 0.3s",
                      marginRight: "10px",
                    }}
                    onMouseEnter={(e) => {
                      e.target.style.backgroundColor = "#c0392b";
                      e.target.style.transform = "scale(1.05)";
                    }}
                    onMouseLeave={(e) => {
                      e.target.style.backgroundColor = "#e74c3c";
                      e.target.style.transform = "scale(1)";
                    }}
                  >
                    Delete
                  </button>
                  <button
                    onClick={() => handleUpdate(c)}
                    style={{
                      padding: "8px",
                      borderRadius: "5px",
                      border: "none",
                      backgroundColor: "#3498db",
                      color: "white",
                      cursor: "pointer",
                      transition: "all 0.3s",
                      marginRight: "10px",
                    }}
                    onMouseEnter={(e) => {
                      e.target.style.backgroundColor = "#2980b9";
                      e.target.style.transform = "scale(1.05)";
                    }}
                    onMouseLeave={(e) => {
                      e.target.style.backgroundColor = "#3498db";
                      e.target.style.transform = "scale(1)";
                    }}
                  >
                    Update
                  </button>
                  <button
                    onClick={() => handleArchive(c)}
                    style={{
                      padding: "8px",
                      borderRadius: "5px",
                      border: "none",
                      backgroundColor: "#8e44ad",
                      color: "white",
                      cursor: "pointer",
                      transition: "all 0.3s",
                    }}
                    onMouseEnter={(e) => {
                      e.target.style.backgroundColor = "#732d91";
                      e.target.style.transform = "scale(1.05)";
                    }}
                    onMouseLeave={(e) => {
                      e.target.style.backgroundColor = "#8e44ad";
                      e.target.style.transform = "scale(1)";
                    }}
                  >
                    Archive
                  </button>
                  <button
                    onClick={() => fetchClientDetails(c.id)} 
                    style={{
                      padding: "8px",
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
                <td style={{ padding: "10px", borderBottom: "1px solid #ddd" }}>
                  {c.companyName}
                </td>
                <td style={{ padding: "10px", borderBottom: "1px solid #ddd" }}>
                  {c.payed}
                  </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

     
      {clientDetails && (
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
            <strong>ID:</strong> {clientDetails.id}
          </p>
          <p>
            <strong>Name:</strong> {clientDetails.firstName}
          </p>
          <p>
            <strong>Last Name:</strong> {clientDetails.lastName}
          </p>
          <p>
            <strong>Case Number:</strong> {clientDetails.caseNumber}
          </p>
          
            <strong>Note:</strong>
            {isEditing ? (
              <div style={{ marginTop: "5px" }}>
                <input
                  type="text"
                  value={editNote || ""}
                  onChange={(e) => setEditNote(e.target.value)}
                  style={{
                    padding: "5px",
                    marginRight: "10px",
                    width: "70%",
                    borderRadius: "5px",
                    border: "1px solid #ccc",
                  }}
                />
                <button
                  onClick={updateClientNote}
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
                  onClick={() => setIsEditing(false)}
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
                {clientDetails.note || "No notes"}
                <button
                  onClick={() => setIsEditing(true)}
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

const tableStyle = {
  width: "100%",
  borderCollapse: "collapse",
  borderRadius: "10px",
  overflow: "hidden",
  boxShadow: "0 4px 10px rgba(0,0,0,0.1)",
};
const theadStyle = { backgroundColor: "#4CAF50", color: "white" };
const thStyle = { padding: "10px", textAlign: "left", borderBottom: "1px solid #ddd" };
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