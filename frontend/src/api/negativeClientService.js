const BASE_URL = "http://localhost:8080/api";
const username = "admin";
const password = "admin123";


export const getNegativeClients = async (authHeader) => {
  return fetchWithAuth(`${BASE_URL}/archived_negative_clients/negative`);
};
export async function updateNegativeClient(id, updatedClient) {
  const response = await fetch(`${BASE_URL}/archived_negative_clients/${id}`, {
    method: "PUT",
    headers: {
      Authorization: getAuthHeader(),
      "Content-Type": "application/json",
    },
    body: JSON.stringify(updatedClient),
  });
  if (!response.ok) throw new Error("Updating error: " + response.status);
  return response.json();
}

export async function deleteNegativeClient(id) {
  const response = await fetch(`${BASE_URL}/archived_negative_clients/${id}`, {
    method: "DELETE",
    headers: { Authorization: getAuthHeader() },
  });
  if (!response.ok) throw new Error("Deleting error: " + response.status);
  return true;
}
export async function searchNegativeClients(params) {
  const query = new URLSearchParams(params).toString();
  const response = await fetch(`${BASE_URL}/archived_negative_clients/search?${query}`, {
    headers: { Authorization: getAuthHeader() },
  });
  if (!response.ok) throw new Error("Search error: " + response.status);
  return response.json();
}
export async function getNegativeDetails(id){
  const response = await fetch(`${BASE_URL}/archived_negative_clients/negativeNode/${id}`, {
    method: "GET",
    headers: {Authorization:getAuthHeader(),
      "Content-Type": "application/json",
    },
  });
  if(!response.ok) throw new Error("Search Error: " + response.status);
  return response.json();
}
export async function updateNegativeDetails(id, note) {
 const response = await fetch(`${BASE_URL}/archived_negative_clients/negativeNotes/${id}`, {
  method: "PUT",
    headers: {Authorization:getAuthHeader(),
    "Content-Type": "application/json",
    
  },
  body: JSON.stringify({ note: note })
  });
  if (!response.ok) {
    const errorText = await response.text();
    console.log("Update error response:", errorText);
    throw new Error("Update Error: " + response.status);
  }
  const updatedData = await response.json(); 
  return updatedData;
}
function getAuthHeader() {
  return localStorage.getItem("authHeader");
}
async function fetchWithAuth(url, options = {}) {
  const response = await fetch(url, {
    ...options,
    headers: {
      ...(options.headers || {}),
      Authorization: getAuthHeader(),
    },
  });

  if (response.status === 401) {
    localStorage.removeItem("authHeader");
       setTimeout(() => {
      window.location.href = "/";
    }, 0);
    return null; 
  }

  if (!response.ok) {
    throw new Error("Error: " + response.status);
  }

  return response.json();
}