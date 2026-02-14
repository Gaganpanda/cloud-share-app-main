import DashboardLayout from "../layout/DashboardLayout.jsx";
import { useState } from "react";
import { useAuth } from "@clerk/clerk-react";
import { AlertCircle } from "lucide-react";
import axios from "axios";
import { apiEndpoints } from "../util/apiEndpoints.js";
import UploadBox from "../components/UploadBox.jsx";

const Upload = () => {
  const [files, setFiles] = useState([]);
  const [uploading, setUploading] = useState(false);
  const [message, setMessage] = useState("");
  const [messageType, setMessageType] = useState("");

  const { getToken } = useAuth();
  const MAX_FILES = 5; // per upload batch only

  const handleFileChange = (e) => {
    const selectedFiles = Array.from(e.target.files);

    if (files.length + selectedFiles.length > MAX_FILES) {
      setMessage(`You can only upload a maximum of ${MAX_FILES} files at once`);
      setMessageType("error");
      return;
    }

    setFiles((prev) => [...prev, ...selectedFiles]);
    setMessage("");
    setMessageType("");
  };

  const handleRemoveFile = (index) => {
    setFiles((prev) => prev.filter((_, i) => i !== index));
  };

  const handleUpload = async () => {
    if (files.length === 0) {
      setMessage("Please select at least one file to upload.");
      setMessageType("error");
      return;
    }

    setUploading(true);
    setMessage("Uploading files...");
    setMessageType("info");

    const formData = new FormData();
    files.forEach((file) => formData.append("files", file));

    try {
      const token = await getToken();

      await axios.post(apiEndpoints.UPLOAD_FILE, formData, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      setMessage("Files uploaded successfully.");
      setMessageType("success");
      setFiles([]);
    } catch (error) {
      setMessage(
        error.response?.data?.message ||
          "Error uploading files. Please try again."
      );
      setMessageType("error");
    } finally {
      setUploading(false);
    }
  };

  return (
    <DashboardLayout activeMenu="Upload">
      <div className="p-6">
        {message && (
          <div
            className={`mb-6 p-4 rounded-lg flex items-center gap-3 ${
              messageType === "error"
                ? "bg-red-50 text-red-700"
                : messageType === "success"
                ? "bg-green-50 text-green-700"
                : "bg-blue-50 text-blue-700"
            }`}
          >
            {messageType === "error" && <AlertCircle size={20} />}
            {message}
          </div>
        )}

        <UploadBox
          files={files}
          onFileChange={handleFileChange}
          onUpload={handleUpload}
          uploading={uploading}
          onRemoveFile={handleRemoveFile}
          isUploadDisabled={uploading || files.length === 0}
        />
      </div>
    </DashboardLayout>
  );
};

export default Upload;
