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
      <ClientFiles clientUuid={clientUuid} />
    </div>
  );
};

export default ClientFilesPage;