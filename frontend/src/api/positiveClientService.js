const BASE_URL = "http://localhost:8080/api";
const username = "admin";
const password = "admin123";

function getAuthHeader() {
  return "Basic " + btoa(`${username}:${password}`);
}


export async function getPositiveClients() {
  const response = await fetch(`${BASE_URL}/archived_positive_clients/positive`, {
    headers: { Authorization: getAuthHeader() },
  });
  if (!response.ok) throw new Error("Ошибка при загрузке клиентов: " + response.status);
  return response.json();
}


export async function updatePositiveClient(id, updatedClient) {
  const response = await fetch(`${BASE_URL}/archived_positive_clients/${id}`, {
    method: "PUT",
    headers: {
      Authorization: getAuthHeader(),
      "Content-Type": "application/json",
    },
    body: JSON.stringify(updatedClient),
  });
  if (!response.ok) throw new Error("Ошибка при обновлении клиента: " + response.status);
  return response.json();
}


export async function deletePositiveClient(id) {
  const response = await fetch(`${BASE_URL}/archived_positive_clients/${id}`, {
    method: "DELETE",
    headers: { Authorization: getAuthHeader() },
  });
  if (!response.ok) throw new Error("Ошибка при удалении клиента: " + response.status);
  return true;
}
export async function searchPositiveClients(params) {
  const query = new URLSearchParams(params).toString();
  const response = await fetch(`${BASE_URL}/archived_positive_clients/search?${query}`, {
    headers: { Authorization: getAuthHeader() },
  });
  if (!response.ok) throw new Error("Ошибка при поиске клиентов: " + response.status);
  return response.json();
}

export async function getPositiveDetails(id){
  const response = await fetch(`${BASE_URL}/archived_positive_clients/positiveNode/${id}`, {
    method: "GET",
    headers: {Authorization:getAuthHeader(),
      "Content-Type": "application/json",
    },
  });
  if(!response.ok) throw new Error("Search Error: " + response.status);
  return response.json();
}
export async function updatePositiveDetails(id,note){
  const response = await fetch (`${BASE_URL}/archived_positive_clients/positiveNotes/${id}`,{
    method: "PUT",
    headers: {Authorization: getAuthHeader(),
      "Content-Type": "application/json",
    },
    body: JSON.stringify(note)
  });
  if (!response.ok) throw new Error("Error while updating client: " + response.status);
  return response.json();
}
