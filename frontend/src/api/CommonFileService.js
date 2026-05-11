import { BASE_URL } from "../apiConfig";

const uploadFile = async (file) => {
        const formData = new FormData();
        formData.append("file",file);

        const response = await fetch(`${BASE_URL}/common_files/upload`, {
                method: "POST",
                body: formData,
                headers: {
                Authorization: getAuthHeader()
            }
        });
        if(!response.ok){
            throw new Error("File upload failed");
        }
        return response.json();
    }

    const getFiles = async () => {
  

    const response = await fetch(
    `${BASE_URL}/common_files`,
    {
      method: "GET",
      headers: {
        Authorization: getAuthHeader(),
        },
      }
    );

    if (!response.ok) {
      throw new Error("Failed to load files");
    }

    return response.json();
  };
    const deleteFile = async (fileId) => {
        const response = await fetch(
            `${BASE_URL}/common_files/${fileId}`,
            {
                method: "DELETE",
                headers: {
                Authorization: getAuthHeader()
                }
            }
        );
        if(!response.ok){
            throw new Error("Delete failed");
        }
        return true;
    } 



    const downloadFile = async (fileId,originalName) => {
        const response = await fetch(
            `${BASE_URL}/common_files/${fileId}/download`,
            {
                method : "GET",
                headers: {
                Authorization: getAuthHeader()
                }
            }
            
        );
        if (!response.ok){
            throw new Error("Download Failed");
        }
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = originalName;
        document.body.appendChild(a);
        a.click();
        a.remove();
        window.URL.revokeObjectURL(url);

    }
    
    
    function getAuthHeader() {
    return localStorage.getItem("authHeader");
    };

    export default {
        downloadFile,
        deleteFile,
        getFiles,
        uploadFile
    }

