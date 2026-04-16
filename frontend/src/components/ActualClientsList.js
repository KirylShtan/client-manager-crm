import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import {
  getActualClients,
  deleteActualClient,
  addActualClient,
  updateActualClient,
  archiveClient,
  searchActualClients,
  getDetails,
  updateDetails,
  searchClientsByDate,
  checkStatus,
  sendNotification,
  sendTelegramNotification,
  getCasePassword
} from "../api/clientService";

const ActualClientsList = () => {
  const [clients, setClients] = useState([]);
  const [newClient, setNewClient] = useState({
    firstName: "",
    lastName: "",
    caseNumber: "",
    status: "",
    companyName: "",
    realPassword: "",
    email: "",
  });
  const [selectedClientId, setSelectedClientId] = useState(null);
  const [clientDetails, setClientDetails] = useState(null);
  const [editNote, setEditNote] = useState("");
  const [isEditing, setIsEditing] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [loadingTelegram, setLoadingTelegram] = useState({});
  const [password, setPassword] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const authHeader = localStorage.getItem("authHeader");

  useEffect(() => {
    if (!authHeader) return;
    getActualClients(authHeader)
      .then(setClients)
      .catch((err) => console.error("Loading Error:", err));
  }, [authHeader]);

  
  const handleAdd = async () => {
    if (Object.values(newClient).some((v) => !v)) return alert("Complete all fields!");
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
        realPassword: "",
        email: "",
      });
    } catch (err) {
      console.error(err);
    }
  };

  const handleDelete = async (id) => {
    await deleteActualClient(id);
    setClients(clients.filter((c) => c.id !== id));
    if (selectedClientId === id) {
      setClientDetails(null);
      setSelectedClientId(null);
    }
  };

  const handleArchive = async (client) => {
    try {
      await archiveClient(client.id);
      setClients(clients.filter((c) => c.id !== client.id));
      alert(`Client ${client.firstName} ${client.lastName} archived!`);
    } catch (err) {
      console.error(err);
      alert("Archive error");
    }
  };

  const handleUpdate = async (client) => {
    const firstName = prompt("First Name:", client.firstName);
    const lastName = prompt("Last Name:", client.lastName);
    const caseNumber = prompt("Case Number:", client.caseNumber);
    const status = prompt("Status:", client.status);
    const submissionDate = prompt("Submission Date (YYYY-MM-DD):", client.submissionDate);
    const companyName = prompt("Company Name:", client.companyName);
    const payed = prompt("Payment Status:", client.payed);

    if (!firstName || !lastName || !caseNumber || !status || !submissionDate || !companyName || !payed) {
      return alert("All fields are required!");
    }

    const updatedClient = { ...client, firstName, lastName, caseNumber, status, submissionDate, companyName, payed };
    try {
      const updated = await updateActualClient(client.id, updatedClient);
      setClients(clients.map((c) => (c.id === client.id ? updated : c)));
    } catch (err) {
      console.error(err);
    }
  };

  const fetchClientDetails = async (id) => {
    try {
      const details = await getDetails(id);
      setClientDetails(details);
      setEditNote(details.note || details.data?.note || "");
      setSelectedClientId(id);
    } catch (err) {
      console.error(err);
      alert("Failed to fetch details");
    }
  };

  const updateClientNote = async () => {
  if (!selectedClientId) return alert("Select a client first");
  try {
    const payload = {
      note: editNote,
      version: clientDetails?.version,
    };
    const updated = await updateDetails(selectedClientId, payload);
    const newNote = updated?.note ?? editNote;
    const newVersion = updated?.version ?? clientDetails?.version;
    setClientDetails({ ...clientDetails, note: newNote, version: newVersion });
    setClients(
      clients.map((c) =>
        c.id === selectedClientId ? { ...c, note: newNote, version: newVersion } : c
      )
    );
    setIsEditing(false);
    alert("Note updated!");
  } catch (err) {
    console.error(err);
    if (err?.status === 409) {
      const freshData = await getDetails(selectedClientId);
      setClientDetails({ ...clientDetails, ...freshData });
      setEditNote(freshData?.note ?? "");
      setClients(clients.map((c) => (c.id === selectedClientId ? { ...c, ...freshData } : c)));
      alert("This note was changed by another user. Latest data loaded.");
      return;
    }
    alert("Failed to update note");
  }
};

  const handleSearch = async () => {
    try {
      const results = await searchActualClients({
        firstName: searchTerm,
        lastName: searchTerm,
        status: searchTerm,
        caseNumber: searchTerm,
        companyName: searchTerm,
        submissionDate: searchTerm,
      });
      setClients(results);
    } catch (err) {
      alert(err.response?.data?.detail || "Search error");
    }
  };

  const handleSearchBetweenDates = async () => {
    try {
      const results = await searchClientsByDate(startDate, endDate);
      setClients(results);
    } catch (err) {
      alert(err.message);
    }
  };


const handleCheckStatus = async (client) => {
  try {
    const realPassword = await getCasePassword(client.id);
    const payload = btoa(
      JSON.stringify({
        caseNumber: client.caseNumber,
        password: realPassword,
      })
    );
    const url =
      `https://www.poznan.uw.gov.pl/cudzoziemcy-stan/?lang=pl` +
      `#autofill=${encodeURIComponent(payload)}`;
    window.open(url, "_blank", "noopener,noreferrer");
  } catch (err) {
    alert("Failed to prepare status check: " + err.message);
  }
};

  const handleSendNotification = async (id, type) => {
    try {
      const result = await sendNotification(id, type);
      alert(result);
    } catch (err) {
      alert("Failed to send notification: " + err.message);
    }
  };

  const handleSendTelegramNotification = async (id) => {
    setLoadingTelegram((prev) => ({ ...prev, [id]: true }));
    try {
      const result = await sendTelegramNotification(id);
      alert(result);
    } catch (err) {
      alert("Telegram notification failed: " + err.message);
    } finally {
      setLoadingTelegram((prev) => ({ ...prev, [id]: false }));
    }
  };

  const handleGetCasePassword = async (id) => {
    setLoading(true);
    setError(null);
    try{
      const result = await getCasePassword(id);
      setPassword(result);
      alert(result);
    }catch(err) {
      alert("Failed to get password from Vault: " + err.message);
      setPassword(null);
    } finally{
      setLoading(false);
    }
  }

  const getStatusColor = (status) => {
    if (!VALID_STATUSES.includes(status)) return "bg-white";
    switch (status.toLowerCase()) {
      case "processing": return "text-yellow-500";
      case "completed": return "text-green-500";
      case "failed": return "text-red-500";
      case "fingerPrints": return "text-purple-500";
      default: return "text-gray-500";
    }
  };

  const VALID_STATUSES = ["Finished", "Processing", "Failed", "FingerPrints"];
  const getRowColor = (status) => {
    switch(status){
      case "Finished": return "bg-violet-300";
      case "Processing": return "bg-green-300"
      case "Failed": return "bg-red-300";
      case "FingerPrints": return "bg-purple-300";
      default: return "bg-white";

    }
  }

  
  return (
    <div className="min-h-screen bg-gradient-to-r from-purple-600 via-pink-500 to-red-500 p-6">
      
      <motion.div className="bg-purple rounded-xl shadow-xl p-6 mb-6"
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
      >
        <h2 className="text-2xl font-bold text-green-500 mb-4 text-center">Adding New Client</h2>
        <div className="flex flex-wrap gap-3">
          {["firstName","lastName","caseNumber","status","companyName","realPassword","email"].map((field) => (
            <input
              key={field}
              type="text"
              placeholder={field}
              value={newClient[field]}
              onChange={(e) => setNewClient({...newClient, [field]: e.target.value})}
              className="flex-1 p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-400"
            />
          ))}
          <button
            onClick={handleAdd}
            className="bg-green-600 text-white px-6 py-2 rounded-lg hover:bg-green-500 transition-transform transform hover:scale-105 shadow-md"
          >
            ADD
          </button>
        </div>
      </motion.div>

      
      <div className="flex gap-3 mb-4">
        <input
          type="text"
          placeholder="Search by name, case..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="flex-1 p-2 rounded-lg border focus:outline-none focus:ring-2 focus:ring-blue-400"
        />
        <button
          onClick={handleSearch}
          className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-500 transition-transform transform hover:scale-105 shadow-md"
        >
          SEARCH
        </button>
      </div>

      
      <div className="flex gap-3 mb-6">
        <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)}
          className="p-2 rounded-lg border focus:outline-none focus:ring-2 focus:ring-blue-400"/>
        <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)}
          className="p-2 rounded-lg border focus:outline-none focus:ring-2 focus:ring-blue-400"/>
        <button onClick={handleSearchBetweenDates}
          className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-500 transition-transform transform hover:scale-105 shadow-md"
        >
          SEARCH BETWEEN DATES
        </button>
      </div>

      
      <div className="overflow-x-auto bg-white rounded-xl shadow-xl">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-green-600 text-white">
            <tr>
              {["ID","Name","Lastname","CaseNumber","SubmissionDate","Status","Operations","Company","Payed"].map((h)=>
                <th key={h} className="px-4 py-2 text-left">{h}</th>
              )}
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {clients.map((c) => (
              <tr key={c.id}
                    className={`${getRowColor(c.status)} hover:bg-gray-200 transition-all`}
                    title={VALID_STATUSES.includes(c.status) ? "" : `Unknown status: ${c.status}. Valid statuses: ${VALID_STATUSES.join(", ")}`}>
                <td className="px-4 py-2">{c.id}</td>
                <td className="px-4 py-2">{c.firstName}</td>
                <td className="px-4 py-2">{c.lastName}</td>
                <td className="px-4 py-2">{c.caseNumber}</td>
                <td className="px-4 py-2">{c.submissionDate}</td>
                <td className={`px-4 py-2 font-bold ${getStatusColor(c.status)}`}>{c.status}</td>
                <td className="px-4 py-2 flex flex-wrap gap-2">
                  <button className="px-3 py-1 bg-blue-500 text-white rounded-lg hover:bg-blue-400" onClick={() => handleUpdate(c)}>Update</button>
                  <button className="px-3 py-1 bg-red-500 text-white rounded-lg hover:bg-red-400" onClick={() => handleDelete(c.id)}>Delete</button>
                  <button className="px-3 py-1 bg-green-500 text-white rounded-lg hover:bg-green-400" onClick={() => handleArchive(c)}>Archive</button>
                  <button className="px-3 py-1 bg-purple-500 text-white rounded-lg hover:bg-purple-400" onClick={() => fetchClientDetails(c.id)}>Details</button>
                  <button className="px-3 py-1 bg-yellow-500 text-white rounded-lg hover:bg-yellow-400" onClick={() => navigate(`/client/${c.clientUuid}/files`)}>Files</button>
                  <button className="px-3 py-1 bg-indigo-500 text-white rounded-lg hover:bg-indigo-400" onClick={() => handleCheckStatus(c)}>Status</button>
                  <button className="px-3 py-1 bg-pink-500 text-white rounded-lg hover:bg-pink-400" onClick={() => handleSendNotification(c.id, "STATUS_CHANGED")}>Notify Email</button>
                  <button className={`px-3 py-1 rounded-lg text-white ${loadingTelegram[c.id] ? "bg-gray-400 cursor-not-allowed" : "bg-teal-500 hover:bg-teal-400"}`} disabled={loadingTelegram[c.id]} onClick={() => handleSendTelegramNotification(c.id)}>
                    {loadingTelegram[c.id] ? "Sending..." : "Telegram"}
                  </button>
                  <button className={`px-3 py-1 rounded-1g text-white rounded-lg ${loading[c.id] ? "bg-gray-400 cursor-not-allowed" : "bg-teal-500 hover:bg-teal-400"}`} disabled={loading[c.id]} onClick={() => handleGetCasePassword(c.id)}>
                    {loading[c.id] ? "Loading..." : "Get Password"}
                  </button>
                </td>
                <td className="px-4 py-2">{c.companyName}</td>
                <td className="px-4 py-2">{c.payed}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      
      {clientDetails && (
        <motion.div className="bg-gray-100 rounded-xl shadow-xl p-6 mt-6"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
        >
          <h3 className="text-green-600 font-bold mb-3">Client Details</h3>
          <p><strong>ID:</strong> {clientDetails.id}</p>
          <p><strong>Name:</strong> {clientDetails.firstName}</p>
          <p><strong>Last Name:</strong> {clientDetails.lastName}</p>
          <p><strong>Case Number:</strong> {clientDetails.caseNumber}</p>
          <p><strong>Note:</strong></p>
          {isEditing ? (
            <div className="flex gap-2 mt-2">
              <input type="text" value={editNote} onChange={(e)=>setEditNote(e.target.value)} className="flex-1 p-2 border rounded-lg"/>
              <button className="px-3 py-1 bg-green-600 text-white rounded-lg" onClick={updateClientNote}>Save</button>
              <button className="px-3 py-1 bg-red-500 text-white rounded-lg" onClick={()=>setIsEditing(false)}>Cancel</button>
            </div>
          ) : (
            <div className="mt-2 flex items-center gap-2">
              <span>{clientDetails.note || "No notes"}</span>
              <button className="px-3 py-1 bg-blue-500 text-white rounded-lg" onClick={()=>setIsEditing(true)}>Edit</button>
            </div>
          )}
        </motion.div>
      )}
    </div>
  );
};

export default ActualClientsList;