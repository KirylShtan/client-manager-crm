import React, { useState, useEffect } from "react";
import clientFileService from "../api/clientFileService";


const ClientFiles = ({ clientUuid }) => {
  const [files, setFiles] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [previewFile, setPreviewFile] = React.useState(null);

  useEffect(() => {
    fetchFiles();
  }, [clientUuid]);
  
  const fetchFiles = async () => {
    try {
      setLoading(true);
      const data = await clientFileService.getClientFiles(clientUuid);
      setFiles(data);
    } catch (error) {
      console.error(error);
      
    } finally {
      setLoading(false);
    }
  };
 const handlePreview = async (file) => {
  try {
    const response = await fetch(
      `http://localhost:8080/api/client_files/${file.id}/download`,
      {
        headers: { Authorization: localStorage.getItem("authHeader") }
      }
    );
    if (!response.ok) throw new Error("Preview failed");

    const blob = await response.blob();
    const url = URL.createObjectURL(blob); 
    setPreviewFile({ url, type: file.contentType });
  } catch (err) {
    console.error(err);
    alert("Preview failed");
  }
};

  const handleFileChange = (e) => {
    setSelectedFile(e.target.files[0]);
  };

  const handleUpload = async () => {
    if (!selectedFile) {
      alert("Choose file to upload");
      return;
    }
    try {
      setLoading(true);
      await clientFileService.uploadFile(clientUuid, selectedFile);
      setSelectedFile(null);
      fetchFiles();
    } catch (error) {
      console.error(error);
      alert("Upload error");
    } finally {
      setLoading(false);
    }
  };

  const handleDownload = async (file) => {
    try {
      await clientFileService.downloadFile(file.id, file.originalName);
    } catch (error) {
      console.error(error);
      alert("Download error");
    }
  };

  const handleDelete = async (file) => {
    if (!window.confirm(`Delete file ${file.originalName}?`)) return;
    try {
      setLoading(true);
      await clientFileService.deleteFile(file.id);
      fetchFiles();
    } catch (error) {
      console.error(error);
      alert("Delete error");
    } finally {
      setLoading(false);
    }
  };

  const buttonStyle = {
    padding: "6px 12px",
    borderRadius: "5px",
    border: "none",
    backgroundColor: "#4CAF50",
    color: "white",
    cursor: "pointer",
    transition: "all 0.3s",
    minWidth: "80px",
    textAlign: "center",
  };

  const tdActionsStyle = { display: "flex", gap: "8px" };
  const fileTableStyle = { width: "100%", borderCollapse: "collapse" };
  const thStyle = { textAlign: "left", borderBottom: "2px solid #ccc", padding: "8px" };
  const tdStyle = { padding: "8px", borderBottom: "1px solid #eee" };

  return (
    <div
      style={{
        padding: "2rem",
        fontFamily: "Arial, sans-serif",
        background: "linear-gradient(135deg, #f0f4f8 0%, #d9e2ec 100%)",
        minHeight: "100vh",
      }}
    >
      <h2>Client Files</h2>
      <div style={{ marginBottom: "1rem", display: "flex", gap: "10px" }}>
        <input type="file" onChange={handleFileChange} />
        <button style={buttonStyle} onClick={handleUpload} disabled={loading || !selectedFile}>
          Upload
        </button>
      </div>

      {loading && <p>Loading...</p>}

      <div
        style={{
          backgroundColor: "#fff",
          padding: "1rem",
          borderRadius: "10px",
          boxShadow: "0 4px 10px rgba(0,0,0,0.1)",
        }}
      >
        {files.length === 0 && !loading ? (
          <p>No files uploaded yet.</p>
        ) : (
          <table style={fileTableStyle}>
            <thead>
              <tr>
                <th style={thStyle}>Name</th>
                <th style={thStyle}>Size (KB)</th>
                <th style={thStyle}>Type</th>
                <th style={thStyle}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {files.map((file) => (
                <tr key={file.id}>
                  <td style={tdStyle}>{file.originalName}</td>
                  <td style={tdStyle}>{(file.size / 1024).toFixed(2)}</td>
                  <td style={tdStyle}>{file.contentType}</td>
                  <td style={tdActionsStyle}>
                    <button
                      style={buttonStyle}
                      onMouseOver={(e) => (e.currentTarget.style.backgroundColor = "#45a049")}
                      onMouseOut={(e) => (e.currentTarget.style.backgroundColor = "#4CAF50")}
                      onClick={() => handleDownload(file)}
                    >
                      Download
                    </button>
                    <button
                      style={buttonStyle}
                      onMouseOver={(e) => (e.currentTarget.style.backgroundColor = "#45a049")}
                      onMouseOut={(e) => (e.currentTarget.style.backgroundColor = "#4CAF50")}
                      onClick={() => handleDelete(file)}
                    >
                      Delete
                    </button>
                    <button
                      style={buttonStyle}
                      onMouseOver={(e) => (e.currentTarget.style.backgroundColor = "#45a049")}
                      onMouseOut={(e) => (e.currentTarget.style.backgroundColor = "#4CAF50")}
                      onClick={() => handlePreview(file)}
                    >
                      Preview
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* --- Preview Modal --- */}
      {previewFile && (
        <div
          style={{
            position: "fixed",
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: "rgba(0,0,0,0.6)",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            zIndex: 1000,
          }}
        >
          <div
            style={{
              background: "#fff",
              padding: "1rem",
              borderRadius: "12px",
              maxWidth: "90%",
              maxHeight: "90%",
              overflow: "auto",
              textAlign: "center",
              boxShadow: "0 5px 15px rgba(0,0,0,0.3)",
            }}
          >
            <button
              onClick={() => {
                window.URL.revokeObjectURL(previewFile.url);
                setPreviewFile(null);
              }}
              style={{ ...buttonStyle, marginBottom: "10px" }}
            >
              Close
            </button>

            {previewFile.type.startsWith("image/") && (
              <img
                src={previewFile.url}
                alt="preview"
                style={{ maxHeight: "70vh", maxWidth: "100%" }}
              />
            )}
            {previewFile.type === "application/pdf" && (
              <iframe
                src={previewFile.url}
                style={{ width: "80vw", height: "70vh" }}
                title="PDF Preview"
              ></iframe>
            )}
            {!previewFile.type.startsWith("image/") &&
              previewFile.type !== "application/pdf" && <p>Preview not available for this file type.</p>}
          </div>
        </div>
      )}
    </div>
  );
};

export default ClientFiles;