import React, { useEffect, useState } from "react";
import {
  getAllCompletedClients,
  deleteCompletedClient,
  searchCompletedClients,
  getCompletedDetails,
  updateCompletedDetails,
  updateCompletedClient
} from "../api/competedSerivce";

const CompletedClientsList = () => {

  const [clients, setClients] = useState([]);
  const [selectedCompletedClientId, setSelectedCompletedClientId] = useState(null);
  const [completedClientDetails, setCompletedClientDetails] = useState(null);
  const [editCompletedNote, setEditCompletedNote] = useState("");
  const [isCompletedEditing, setCompletedIsEditing] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");

  
  useEffect(() => {
    getAllCompletedClients()
      .then(setClients)
      .catch((err) => console.error("Loading error:", err));
  }, []);

 
  const handleDelete = async (id) => {
    await deleteCompletedClient(id);
    setClients(clients.filter((c) => c.id !== id));
    if (selectedCompletedClientId === id) {
      setCompletedClientDetails(null); 
      setSelectedCompletedClientId(null);
  }
}

  
  const handleUpdate = async (client) => {
    const firstName = prompt("input name: ", client.firstName);
    const lastName = prompt("input lastname: ", client.lastName);
    const caseNumber = prompt("input casenumber: ", client.caseNumber);
    const status = prompt("input status: ", client.status);
    const submissionDate = prompt("input submissiondate (YYYY-MM-DD): ", client.submissionDate);
    const companyName = prompt("input companyname: ", client.companyName);
    const payed = prompt("Input payed status:  ", client.payed)
    

    if (!firstName || !lastName || !caseNumber || !status || !submissionDate || !companyName || !payed) {
      alert("All fields are necessary!");
      return;
    }

    const updatedClient = { ...client, firstName, lastName, caseNumber, status, submissionDate, companyName, payed };

    try {
      const updated = await updateCompletedClient(client.id, updatedClient);
      setClients(clients.map((c) => (c.id === client.id ? updated : c)));
    } catch (err) {
      console.error("Updating error:", err);
    }
  };

  
  const handleSearch = async () => {
    try {
      const results = await searchCompletedClients({
        firstName: searchTerm,
        lastName: searchTerm,
        caseNumber: searchTerm,
        status: searchTerm,
        companyName: searchTerm,
        submissionDate: searchTerm,
      });
      setClients(results);
    } catch (err) {
      alert("Invalid data pattern, expected yyyy-MM-dd");
      console.error(err);
    }
  };

  
  const fetchCompletedClientsDetails = async (id) => {
    console.log("Fetching details for id:", id);
    try {
      const details = await getCompletedDetails(id);
      console.log("Raw response from getDetails:", details);
      if (!details || typeof details !== "object") {
        throw new Error("Invalid or empty response from server");
      }
      setCompletedClientDetails(details);
      const note = details.note !== undefined ? details.note : (details.data?.note || "");
      if (note === undefined) {
        console.warn("Note field not found in response:", details);
      }
      setEditCompletedNote(note);
      setSelectedCompletedClientId(id);
    } catch (err) {
      console.error("Error fetching details:", err.message);
      alert(`Failed to load details: ${err.message}. Check console for more info.`);
    }
  };
  
    const updateCompletedNote = async () => {
    console.log("Updating note for id:", selectedCompletedClientId, "with note:", editCompletedNote);
    if (!selectedCompletedClientId) {
      console.error("No client selected for update");
      alert("Please select a client to update.");
      return;
    }
  
    try {
      const response = await updateCompletedDetails(selectedCompletedClientId, editCompletedNote);
      console.log("Update response:", response);
  
     
      let updatedData;
      if (response.data && typeof response.data === "object") {
        updatedData = response.data;
      } else if (response.note !== undefined) {
        updatedData = response; 
      } else {
        updatedData = { note: editCompletedNote }; 
        console.warn("Unexpected response format, using editNote as fallback:", response);
      }
  
      const newNote = updatedData.note || editCompletedNote; 
      setCompletedClientDetails({ ...completedClientDetails, note: newNote }); 
      setClients(
        clients.map((c) =>
          c.id === selectedCompletedClientId ? { ...c, note: newNote } : c 
        )
      );
      setCompletedIsEditing(false);
      alert("Note updated successfully!");
    } catch (err) {
      console.error("Error updating note:", err.message);
      alert("Failed to update note: " + err.message);
    }
  };



return (
    <div style={{
      maxWidth: "900px",
      margin: "30px auto",
      padding: "25px",
      borderRadius: "15px",
      boxShadow: "0 4px 15px rgba(0, 0, 0, 0.1)",
      fontFamily: "Arial, sans-serif",
      transition: "all 0.3s ease-in-out",
      animation: "fadeIn 0.6s ease-in-out", }}>
      <div style={{ textAlign: "center", marginBottom: "20px", color: "#C62828" }}>
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
              backgroundColor: "#C62828",
              color: "white",
              cursor: "pointer",
            }}
            onMouseEnter={(e) => {
            e.currentTarget.style.backgroundColor = "#C6282";
            e.currentTarget.style.transform = "scale(1.01)";   
            }}
          >
            SEARCH
          </button>
        </div>
        <div style={{
              textAlign: "center",
              marginBottom: "20px",
              color: "#e32121", 
              fontFamily: "'Poppins', 'Arial', sans-serif", 
              fontSize: "28px", 
              fontWeight: "700", 
              letterSpacing: "1.2px", 
              textShadow: "1px 1px 4px rgba(0,0,0,0.2)", 
              textTransform: "uppercase", 
              animation: "fadeIn 0.6s ease-in-out" 
              }}>
        COMPLETED CLIENTS
      </div>
      </div>
      <table
        style={{
          width: "125%",
          borderCollapse: "collapse",
          boxShadow: "0 4px 10px rgba(0,0,0,0.1)",
          borderRadius: "10px",
          overflow: "hidden",
          marginLeft: "-80px",
          marginRight: "auto",
          
        }}
      >
        <thead style={{ backgroundColor: "red",
        overflowX: "auto",
          overflow: "hidden",
          borderRadius: "10px",
          width: "118%",
          fontFamily: "'Poppins', 'Segoe UI', Roboto, sans-serif",
          fontSize: "15px",                                        
          letterSpacing: "0.3px",

         }}>
          <tr>
            <th style={thStyle}>ID</th>
            <th style={thStyle}>name</th>
            <th style={thStyle}>lastname</th>
            <th style={thStyle}>casenumber</th>
            <th style={thStyle}>submissiondate</th>
            <th style={thStyle}>status</th>
            <th style={{ ...thStyle, textAlign: "center" }}>operations</th>
            <th style={thStyle}>companyName</th>
            <th style={thStyle}>payed</th>
          </tr>
        </thead>
        <tbody>
          {clients.map((c) => (
            <tr key={c.id} style={{
            backgroundColor: "#fff9f9",
            transition: "background-color 0.3s ease, transform 0.2s ease",
            }}
            onMouseEnter={(e) => {
            e.currentTarget.style.backgroundColor = "#fde0dc";
            e.currentTarget.style.transform = "scale(1.01)";
            }}
            onMouseLeave={(e) => {
            e.currentTarget.style.backgroundColor = "#fff9f9";
            e.currentTarget.style.transform = "scale(1)";
            }}
>
              <td style={tdStyle}>{c.id}</td>
              <td style={tdStyle}>{c.firstName}</td>
              <td style={tdStyle}>{c.lastName}</td>
              <td style={tdStyle}>{c.caseNumber}</td>
              <td style={tdStyle}>{c.submissionDate}</td>
              <td style={tdStyle}>{c.status}</td>
              <td style={{ ...tdStyle, minWidth: "200px", display: "flex", flexWrap: "nowrap", justifyContent: "flex-start", alignItems: "center", overflow: "hidden" }}>
                <button style={btnUpdate} onClick={() => handleUpdate(c)}
                  onMouseEnter={(e) => {
                  e.target.style.backgroundColor = "#1E88E5";
                  e.target.style.transform = "scale(1.05)";
                  e.target.style.boxShadow = "0 4px 12px rgba(0,0,0,0.3)";
                  
                  }}
                  onMouseLeave={(e) => {
                 e.target.style.backgroundColor = "#42A5F5";
                  e.target.style.transform = "scale(1)";
                  e.target.style.boxShadow = "0 2px 5px rgba(0,0,0,0.2)";
                  }}>
                  Update
                </button>
                <button style={btnDelete} onClick={() => handleDelete(c.id)}
                  onMouseEnter={(e) => {
                  e.target.style.backgroundColor = "#E53935";
                  e.target.style.transform = "scale(1.05)";
                  }}
                  onMouseLeave={(e) => {
                  e.target.style.backgroundColor = "#E53935";
                  e.target.style.transform = "scale(1)";
                  }}>
                  Delete
                </button>
                <button 
                onClick ={() => fetchCompletedClientsDetails(c.id)}
                style={{
                      padding: "8px",
                      borderRadius: "5px",
                      border: "none",
                      backgroundColor: "#9b59b6",
                      color: "white",
                      cursor: "pointer",
                      transition: "all 0.3s",
                      marginLeft: "10px"
                      
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
              <td style={tdStyle}>{c.payed}</td>
            </tr>
          ))}
        </tbody>
      </table>
      {completedClientDetails && (
        <div
          style={{
      backgroundColor: "#fff",
      padding: "20px",
      borderRadius: "10px",
      marginTop: "25px",
      border: "1px solid #e0e0e0",
      boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
      animation: "fadeIn 0.4s ease-in-out",
      transition: "transform 0.3s ease, box-shadow 0.3s ease",
    }}
    onMouseEnter={(e) => {
      e.currentTarget.style.transform = "scale(1.02)";
      e.currentTarget.style.boxShadow = "0 6px 18px rgba(0,0,0,0.15)";
    }}
    onMouseLeave={(e) => {
      e.currentTarget.style.transform = "scale(1)";
      e.currentTarget.style.boxShadow = "0 4px 12px rgba(0,0,0,0.1)";
    }}
        >
          <h3 style={{ marginBottom: "10px", color: "green" }}>
            Client Details
          </h3>
          <p>
            <strong>ID:</strong> {completedClientDetails.id}
          </p>
          <p>
            <strong>Name:</strong> {completedClientDetails.firstName}
          </p>
          <p>
            <strong>Last Name:</strong> {completedClientDetails.lastName}
          </p>
          <p>
            <strong>Case Number:</strong> {completedClientDetails.caseNumber}
          </p>
          
            <strong>CompletedNote:</strong>
            {isCompletedEditing ? (
              <div style={{ marginTop: "5px" }}>
                <input
                  type="text"
                  value={editCompletedNote || ""}
                  onChange={(e) => setEditCompletedNote(e.target.value)}
                  style={{
                    padding: "5px",
                    marginRight: "10px",
                    width: "70%",
                    borderRadius: "5px",
                    border: "1px solid #ccc",
                  }}
                />
                <button
                  onClick={updateCompletedNote}
                  style={{
                    padding: "5px 10px",
                    borderRadius: "5px",
                    border: "none",
                    backgroundColor: "#27ae71ff",
                    color: "white",
                    cursor: "pointer",
                  }}
                >
                  Save
                </button>
                <button
                  onClick={() => setCompletedIsEditing(false)}
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
                {completedClientDetails.note || "No notes"}
                <button
                  onClick={() => setCompletedIsEditing(true)}
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
  fontFamily: "'Poppins', 'Segoe UI', Roboto, sans-serif",
  overflow: "hidden",
  
  
};

const btnUpdate = {
  marginRight: "10px",
  padding: "6px 12px",
  borderRadius: "5px",
  border: "none",
  backgroundColor: "#42A5F5",
  color: "white",
  cursor: "pointer",
  transition: "all 0.3s"
  
};

const btnDelete = {
  padding: "6px 12px",
  borderRadius: "5px",
  border: "none",
  backgroundColor: "#E53935",
  color: "white",
  cursor: "pointer",
  transition: "all 0.3s"
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

export default CompletedClientsList;