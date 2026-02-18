import React from "react";
import { useParams, Link } from "react-router-dom";
import ClientFiles from "./ClientFiles";

const ClientFilesPage = () => {
  const { clientUuid } = useParams(); 
  if (!clientUuid) {
  return <div>Invalid client UUID</div>;
}

  return (
    <div style={{ padding: "20px", color: "black" }}>
      <Link to="/" style={{ color: "#50fa7b", marginBottom: "20px", display: "inline-block" }}>
        ← Back
      </Link>
      <h1>Files for Client {clientUuid}</h1>
      <ClientFiles clientUuid={clientUuid} />
    </div>
  );
};

export default ClientFilesPage;