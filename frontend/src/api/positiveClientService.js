const BASE_URL = "http://localhost:8080/api";
const username = "admin";
const password = "admin123";

function getAuthHeader() {
  return "Basic " + btoa(`${username}:${password}`);
}

// Получить всех клиентов
export async function getPositiveClients() {
  const response = await fetch(`${BASE_URL}/archived_positive_clients/positive`, {
    headers: { Authorization: getAuthHeader() },
  });
  if (!response.ok) throw new Error("Ошибка при загрузке клиентов: " + response.status);
  return response.json();
}

// Обновить клиента
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

// Удалить клиента
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
