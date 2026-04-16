import React, { useState, useEffect } from "react";
import commonFileService from "../api/CommonFileService";
const CommonFiles = () => {
  const [files, setFiles] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [previewFile, setPreviewFile] = useState(null);
  useEffect(() => {
    fetchFiles();
  }, []);
  const fetchFiles = async () => {
    try {
      setLoading(true);
      const data = await commonFileService.getFiles();
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
        `http://localhost:8080/api/common_files/${file.id}/download`,
        { headers: { Authorization: localStorage.getItem("authHeader") } }
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
  const handleFileChange = (e) => setSelectedFile(e.target.files[0]);
  const handleUpload = async () => {
    if (!selectedFile) return alert("Choose file to upload");
    try {
      setLoading(true);
      await commonFileService.uploadFile(selectedFile);
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
      await commonFileService.downloadFile(file.id, file.originalName);
    } catch (error) {
      console.error(error);
      alert("Download error");
    }
  };
  const handleDelete = async (file) => {
    if (!window.confirm(`Delete file ${file.originalName}?`)) return;
    try {
      setLoading(true);
      await commonFileService.deleteFile(file.id);
      fetchFiles();
    } catch (error) {
      console.error(error);
      alert("Delete error");
    } finally {
      setLoading(false);
    }
  };
  const buttonStyle = {
    padding: "8px 16px",
    borderRadius: "10px",
    border: "none",
    backgroundColor: "#6a11cb",
    backgroundImage: "linear-gradient(135deg, #6a11cb, #2575fc)",
    color: "white",
    fontWeight: "bold",
    cursor: "pointer",
    transition: "all 0.3s",
  };
  const tdActionsStyle = { display: "flex", gap: "10px" };
  const fileTableStyle = { width: "100%", borderCollapse: "collapse" };
  const thStyle = { textAlign: "left", borderBottom: "2px solid #ccc", padding: "10px" };
  const tdStyle = { padding: "10px", borderBottom: "1px solid #eee" };
  return (
    <div
      style={{
        minHeight: "100vh",
        padding: "2rem",
        fontFamily: "'Poppins', sans-serif",
        position: "relative",
        overflow: "hidden",
        background: "linear-gradient(120deg, #f6d365, #fda085)",
        backgroundSize: "400% 400%",
        animation: "backgroundMove 15s ease infinite",
      }}
    >
      <style>
        {`
          @keyframes backgroundMove {
            0% { background-position: 0% 50%; }
            50% { background-position: 100% 50%; }
            100% { background-position: 0% 50%; }
          }
        `}
      </style>
      <div
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          marginBottom: "1.5rem",
          flexWrap: "wrap",
        }}
      >
        <button
          onClick={() => window.history.back()}
          style={{
            display: "flex",
            alignItems: "center",
            gap: "5px",
            padding: "8px 15px",
            borderRadius: "12px",
            border: "none",
            backgroundColor: "#ff7e5f",
            backgroundImage: "linear-gradient(135deg, #ff7e5f, #feb47b)",
            color: "white",
            fontWeight: "bold",
            cursor: "pointer",
            fontSize: "1rem",
            transition: "all 0.3s",
          }}
          onMouseEnter={(e) => (e.currentTarget.style.transform = "scale(1.1)")}
          onMouseLeave={(e) => (e.currentTarget.style.transform = "scale(1)")}
        >
          &#8592; Back
        </button>
        <h1
          style={{
            color: "#fff",
            fontSize: "2rem",
            fontWeight: "bold",
            textShadow: "2px 2px 8px rgba(0,0,0,0.3)",
            margin: 0,
          }}
        >
          Common Storage
        </h1>
      </div>
      <div style={{ marginBottom: "1rem", display: "flex", gap: "10px", justifyContent: "center" }}>
        <input type="file" onChange={handleFileChange} />
        <button style={buttonStyle} onClick={handleUpload} disabled={loading || !selectedFile}>
          Upload
        </button>
      </div>
      {loading && <p style={{ textAlign: "center", color: "#fff" }}>Loading...</p>}
      <div
        style={{
          backgroundColor: "rgba(255, 255, 255, 0.95)",
          padding: "1rem",
          borderRadius: "15px",
          boxShadow: "0 8px 20px rgba(0,0,0,0.2)",
          overflowX: "auto",
        }}
      >
        {files.length === 0 && !loading ? (
          <p style={{ textAlign: "center" }}>No files uploaded yet.</p>
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
                <tr
                  key={file.id}
                  style={{ transition: "transform 0.3s, box-shadow 0.3s" }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.transform = "scale(1.02)";
                    e.currentTarget.style.boxShadow = "0 8px 15px rgba(0,0,0,0.2)";
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.transform = "scale(1)";
                    e.currentTarget.style.boxShadow = "none";
                  }}
                >
                  <td style={tdStyle}>{file.originalName}</td>
                  <td style={tdStyle}>{(file.size / 1024).toFixed(2)}</td>
                  <td style={tdStyle}>{file.contentType}</td>
                  <td style={tdActionsStyle}>
                    <button style={buttonStyle} onClick={() => handleDownload(file)}>
                      Download
                    </button>
                    <button style={buttonStyle} onClick={() => handleDelete(file)}>
                      Delete
                    </button>
                    <button style={buttonStyle} onClick={() => handlePreview(file)}>
                      Preview
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
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
              boxShadow: "0 5px 20px rgba(0,0,0,0.3)",
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
              <img src={previewFile.url} alt="preview" style={{ maxHeight: "70vh", maxWidth: "100%" }} />
            )}
            {previewFile.type === "application/pdf" && (
              <iframe src={previewFile.url} style={{ width: "80vw", height: "70vh" }} title="PDF Preview" />
            )}
            {!previewFile.type.startsWith("image/") && previewFile.type !== "application/pdf" && (
              <p>Preview not available for this file type.</p>
            )}
          </div>
        </div>
      )}
    </div>
  );
};
export default CommonFiles;