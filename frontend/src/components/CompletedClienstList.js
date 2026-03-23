import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
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
  const [selectedClientId, setSelectedClientId] = useState(null);
  const [clientDetails, setClientDetails] = useState(null);
  const [editNote, setEditNote] = useState("");
  const [isEditing, setIsEditing] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");

  const navigate = useNavigate();

  useEffect(() => {
    getAllCompletedClients().then(setClients).catch(console.error);
  }, []);

  const handleDelete = async (id) => {
    await deleteCompletedClient(id);
    setClients(clients.filter(c => c.id !== id));
    if (selectedClientId === id) {
      setClientDetails(null);
      setSelectedClientId(null);
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
      const updated = await updateCompletedClient(client.id, updatedClient);
      setClients(clients.map(c => c.id === client.id ? updated : c));
    } catch (err) {
      console.error(err);
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
        submissionDate: searchTerm
      });
      setClients(results);
    } catch (err) {
      alert("Search failed. Check the format.");
      console.error(err);
    }
  };

  const fetchDetails = async (id) => {
    try {
      const details = await getCompletedDetails(id);
      setClientDetails(details);
      setEditNote(details.note || details.data?.note || "");
      setSelectedClientId(id);
    } catch (err) {
      alert("Failed to load details");
      console.error(err);
    }
  };

  const updateNote = async () => {
    if (!selectedClientId) return alert("Select a client first");
    try {
      const res = await updateCompletedDetails(selectedClientId, editNote);
      const newNote = res?.data?.note || editNote;
      setClientDetails({ ...clientDetails, note: newNote });
      setClients(clients.map(c => c.id === selectedClientId ? { ...c, note: newNote } : c));
      setIsEditing(false);
      alert("Note updated!");
    } catch (err) {
      alert("Failed to update note");
      console.error(err);
    }
  };

  const getStatusColor = (status) => {
    switch(status.toLowerCase()) {
      case "completed": return "text-green-500 font-bold";
      case "failed": return "text-red-500 font-bold";
      default: return "text-gray-500 font-bold";
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-r from-purple-600 via-pink-500 to-red-500 p-6">
      
      <div className="flex gap-3 mb-6">
        <input
          type="text"
          placeholder="Search by name, case, company..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="flex-1 p-2 rounded-lg border focus:outline-none focus:ring-2 focus:ring-blue-400"
        />
        <button
          onClick={handleSearch}
          className="bg-red-600 text-white px-6 py-2 rounded-lg hover:bg-red-500 shadow-md transition-transform transform hover:scale-105"
        >
          SEARCH
        </button>
      </div>

      <h2 className="text-center text-3xl font-bold text-white mb-6 drop-shadow-lg">COMPLETED CLIENTS</h2>

      
      <div className="overflow-x-auto bg-white rounded-xl shadow-xl">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-red-600 text-white">
            <tr>
              {["ID","Name","Lastname","CaseNumber","SubmissionDate","Status","Operations","Company","Payed"].map(h => (
                <th key={h} className="px-4 py-2 text-left">{h}</th>
              ))}
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {clients.map(c => (
              <tr key={c.id} className="hover:bg-red-50 transition-all">
                <td className="px-4 py-2">{c.id}</td>
                <td className="px-4 py-2">{c.firstName}</td>
                <td className="px-4 py-2">{c.lastName}</td>
                <td className="px-4 py-2">{c.caseNumber}</td>
                <td className="px-4 py-2">{c.submissionDate}</td>
                <td className={`px-4 py-2 ${getStatusColor(c.status)}`}>{c.status}</td>
                <td className="px-4 py-2 flex flex-wrap gap-2">
                  <button className="px-3 py-1 bg-blue-500 text-white rounded-lg hover:bg-blue-400" onClick={() => handleUpdate(c)}>Update</button>
                  <button className="px-3 py-1 bg-red-500 text-white rounded-lg hover:bg-red-400" onClick={() => handleDelete(c.id)}>Delete</button>
                  <button className="px-3 py-1 bg-purple-500 text-white rounded-lg hover:bg-purple-400" onClick={() => fetchDetails(c.id)}>Details</button>
                  <button className="px-3 py-1 bg-indigo-500 text-white rounded-lg hover:bg-indigo-400" onClick={() => navigate(`/client/${c.clientUuid}/files`)}>Files</button>
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
              <input type="text" value={editNote} onChange={(e) => setEditNote(e.target.value)}
                className="flex-1 p-2 border rounded-lg"/>
              <button className="px-3 py-1 bg-green-600 text-white rounded-lg" onClick={updateNote}>Save</button>
              <button className="px-3 py-1 bg-red-500 text-white rounded-lg" onClick={() => setIsEditing(false)}>Cancel</button>
            </div>
          ) : (
            <div className="mt-2 flex items-center gap-2">
              <span>{clientDetails.note || "No notes"}</span>
              <button className="px-3 py-1 bg-blue-500 text-white rounded-lg" onClick={() => setIsEditing(true)}>Edit</button>
            </div>
          )}
        </motion.div>
      )}
    </div>
  );
};

export default CompletedClientsList;