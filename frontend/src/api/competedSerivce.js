const BASE_URL = "http://localhost:8080/api";



export const getAllCompletedClients = async () => {
  const data = await fetchWithAuth(`${BASE_URL}/completed_clients/all_completed_clients`);
  console.log("Completed clients data:", data);
  return Array.isArray(data) ? data : data.data ?? [];
};

export async function  updateCompletedClient(id,updatedClient){
    const response = await fetch(`${BASE_URL}/completed_clients/${id}`,
        {
            method: "PUT",
            headers:{
                Authorization: getAuthHeader(),
                "Content-Type": "application/json"
            },
            body: JSON.stringify(updatedClient)
        });
        if (!response.ok) throw new Error("Updating error: " + response.status);
        return response.json();
}

export async function deleteCompletedClient(id){
    const response = await fetch(`${BASE_URL}/completed_clients/${id}`,{
        method: "DELETE",
        headers: {
            Authorization: getAuthHeader()
        }
    });
    if (!response.ok) throw new Error("Deleting error: " + response.status);
    return true;
}

export async function searchCompletedClients(params){
    const query = new URLSearchParams(params).toString();
    const response = await fetch(`${BASE_URL}/completed_clients/completed_search/search?${query}`,{
        headers: {Authorization: getAuthHeader()},
    });
    if (!response.ok) throw new Error ("Search Error: " + response.status);
    return response.json();
}

export async function getCompletedDetails(id){
    const url = `${BASE_URL}/completed_clients/Note/${id}`;
          console.log("BASE_URL:", BASE_URL);
          console.log("Sending request to:", url);
          const response = await fetch (url,{
        method:"GET",
        headers: {Authorization: getAuthHeader(),
            "Content-Type": "application/json",
        }
    });
    if (!response.ok) throw new Error ("Searching Note error: " + response.status);
    return response.json();
}

export async function updateCompletedDetails(id,note){
    const response = await fetch (`${BASE_URL}/completed_clients/completedNoteUpdate/${id}`,{
        method: "PUT",
        headers: {Authorization: getAuthHeader(),
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ note : note })
    });
    if (!response.ok){
        const errorText = await response.text();
        console.log("Update error response: ", errorText);
        throw new Error ("Update error: " + response.status);
    }
    return response.json();
}

function getAuthHeader(){
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
    alert("Unauthorized: check credentials");
    window.location.href = "/";
    return null;
  }

  if (!response.ok) {
    throw new Error("Error: " + response.status);
  }

  return response.json();
}
