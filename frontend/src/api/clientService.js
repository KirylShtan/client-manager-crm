const BASE_URL = "http://localhost:8080/api";

export const getActualClients = async (authHeader) => {
  return fetchWithAuth(`${BASE_URL}/ActualClients/actual`);
};
export async function archiveClient(id, isPositive) {
  const response = await fetch(`${BASE_URL}/ActualClients/${id}/archive`, {
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

export async function getDetails(id) {
  const url = `${BASE_URL}/ActualClients/actualNode/${id}`;
  console.log("BASE_URL:", BASE_URL);
  console.log("Sending request to:", url);
  const response = await fetch(url, {
    method: "GET",
    headers: {
      Authorization: getAuthHeader(),
      "Content-Type": "application/json",
    },
  });
  if (!response.ok) {
    const errorText = await response.text();
    console.log("Server error response:", errorText);
    throw new Error("Search Error: " + response.status + " - " + errorText);
  }
  return response.json();
}
export async function updateDetails(id,note){
  const response = await fetch (`${BASE_URL}/ActualClients/${id}/notes`,{
    method: "PUT",
    headers: {Authorization: getAuthHeader(),
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ note: note })
  });
  if (!response.ok) throw new Error("Error while updating client: " + response.status);
  return response.json();
}

export async function searchClientsByDate(startDate, endDate) {
  const query = new URLSearchParams({ startDate, endDate }).toString();
  const response = await fetch(`${BASE_URL}/ActualClients/complexDate?${query}`, {
    headers: { Authorization: getAuthHeader() },
  });
   if (!response.ok) {
    const data = await response.json().catch(() => null);
    const message = data?.detail || "Error fetching clients";
    throw new Error(message);
  }

  return response.json();
}

export async function checkStatus(clientId) {
  const authHeader = localStorage.getItem("authHeader");
  if (!authHeader) throw new Error("User not authenticated");
  const response = await fetch(`${BASE_URL}/ActualClients/check-status/${clientId}`, {
    method: "GET",
    headers: { Authorization: authHeader, Accept: "text/html" },
  });
  if (!response.ok) throw new Error(`Status bridge failed: ${response.status}`);
  const html = await response.text();
  const tab = window.open("", "_blank"); // remove noopener for now
  if (!tab) throw new Error("Popup blocked");
  tab.document.open();
  tab.document.write(html);
  tab.document.close();
  
  const form = tab.document.getElementById("govForm");
  if (form) form.submit();
  else throw new Error("Bridge form not found");
}


export async function sendNotification(clientId, type) {
  const authHeader = localStorage.getItem("authHeader");
  if (!authHeader) {
    console.error("No authHeader found in localStorage!");
    throw new Error("User not authenticated");
  }

  try {
    
    const response = await fetch(
      `api/notifications/send?clientId=${clientId}&type=${type}`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": authHeader,
        },
      }
    );

    const result = await response.text(); 

    if (!response.ok) {
      throw new Error(`Server error: ${response.status} - ${result}`);
    }

    return result; 
  } catch (err) {
    console.error("Error while sending notification:", err);
    throw err;
  }
}
export async function sendTelegramNotification(clientId, webhookSecret) {
  const authHeader = localStorage.getItem("authHeader");
  if (!authHeader) {
    console.error("No authHeader found in localStorage!");
    throw new Error("User not authenticated");
  }

  try {
    const response = await fetch(`/api/telegram/notifyStatus?clientId=${clientId}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": authHeader,
        "X-Telegram-Bot-Api-Secret-Token": webhookSecret
      }
    });

    const result = await response.text();
    

    if (!response.ok) {
      throw new Error(`Server error: ${response.status} - ${result}`);
    }

    return result;
  } catch (err) {
    console.error("Error while sending notification:", err);
    throw err;
  }
}
export async function getCasePassword(clientId){
  const authHeader = localStorage.getItem("authHeader");
  if(!authHeader){
    console.error("No authHeader found in localStorage!");
    throw new Error("User not authenticated");
  }

  try {
    const response = await fetch(`${BASE_URL}/ActualClients/realCasePassword/${clientId}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Authorization": authHeader
      }
    });

    if (response.ok) {
      return await response.text(); 
    } else {
      const errorBody = await response.json();
      throw new Error(`${response.status} - ${errorBody.detail || errorBody.title}`);
    }

  } catch(err) {
    console.error(`Error while getting password for client ${clientId}`, err);
    throw err;
  }
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