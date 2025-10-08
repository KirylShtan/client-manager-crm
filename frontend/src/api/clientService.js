const BASE_URL = "http://localhost:8080/api";
const username = "admin";
const password = "admin123";

function getAuthHeader() {
  return "Basic " + btoa(`${username}:${password}`);
}

export async function getActualClients() {
  const response = await fetch(`${BASE_URL}/ActualClients/actual`, {
    headers: { Authorization: getAuthHeader() },
  });
  if (!response.ok) throw new Error("Error while trying to upload all ActualClients: " + response.status);
  return response.json();
}
export async function archiveClient(id, isPositive) {
  const response = await fetch(`${BASE_URL}/ActualClients/${id}/archive?isPositive=${isPositive}`, {
    method: "POST",
    headers: { Authorization: getAuthHeader() },
  });

  if (!response.ok) throw new Error("Archive error: " + response.status);
  return true;
}

export async function addActualClient(client) {
  const response = await fetch(`${BASE_URL}/ActualClients/add`, {
    method: "POST",
    headers: {
      Authorization: getAuthHeader(),
      "Content-Type": "application/json",
    },
    body: JSON.stringify(client),
  });
  if (!response.ok) throw new Error("Error while adding new client: " + response.status);
  return response.json();
}
export async function updateActualClient(id, updatedClient) {
  const response = await fetch(`${BASE_URL}/ActualClients/${id}`, {
    method: "PUT",
    headers: {
      Authorization: getAuthHeader(),
      "Content-Type": "application/json",
    },
    body: JSON.stringify(updatedClient),
  });
  if (!response.ok) throw new Error("Error while updating client: " + response.status);
  return response.json();
}

export async function deleteActualClient(id) {
  const response = await fetch(`${BASE_URL}/ActualClients/${id}`, {
    method: "DELETE",
    headers: { Authorization: getAuthHeader() },
  });
  if (!response.ok) throw new Error("Error while deleting client: " + response.status);
  return true;
}
export async function searchActualClients(params) {
  const query = new URLSearchParams(params).toString();
  const response = await fetch(`${BASE_URL}/ActualClients/search?${query}`, {
    headers: { Authorization: getAuthHeader() },
  });
  if (!response.ok) throw new Error("Search Error: " + response.status);
  return response.json();
}