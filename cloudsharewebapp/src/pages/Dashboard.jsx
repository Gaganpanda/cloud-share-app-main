import DashboardLayout from "../layout/DashboardLayout.jsx";
import { useAuth } from "@clerk/clerk-react";
import { useEffect, useState } from "react";
import axios from "axios";
import { apiEndpoints } from "../util/apiEndpoints.js";
import { Loader2 } from "lucide-react";
import DashboardUpload from "../components/DashboardUpload.jsx";
import RecentFiles from "../components/RecentFiles.jsx";

const Dashboard = () => {
  const [recentFiles, setRecentFiles] = useState([]);
  const [selectedFiles, setSelectedFiles] = useState([]);
  const [uploading, setUploading] = useState(false);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [messageType, setMessageType] = useState("");

  const { getToken } = useAuth();
  const MAX_FILES = 5;

  // ===============================
  // FETCH RECENT FILES
  // ===============================
  const fetchRecentFiles = async () => {
    try {
      setLoading(true);
      const token = await getToken();

      const res = await axios.get(apiEndpoints.FETCH_FILES, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      const sorted = res.data
        .sort((a, b) => new Date(b.uploadedAt) - new Date(a.uploadedAt))
        .slice(0, 5);

      setRecentFiles(sorted);
    } catch (err) {
      console.error("Error fetching files:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRecentFiles();
  }, []);

  // ===============================
  // HANDLE FILE SELECT
  // ===============================
  const handleFileChange = (e) => {
    const newFiles = Array.from(e.target.files);

    if (selectedFiles.length + newFiles.length > MAX_FILES) {
      setMessage(`You can upload maximum ${MAX_FILES} files at once.`);
      setMessageType("error");
      return;
    }

    setSelectedFiles((prev) => [...prev, ...newFiles]);
    setMessage("");
  };

  // ===============================
  // REMOVE FILE
  // ===============================
  const handleRemoveFile = (index) => {
    setSelectedFiles((prev) => prev.filter((_, i) => i !== index));
  };

  // ===============================
  // HANDLE UPLOAD
  // ===============================
  const handleUpload = async () => {
    if (selectedFiles.length === 0) {
      setMessage("Select at least one file.");
      setMessageType("error");
      return;
    }

    try {
      setUploading(true);
      setMessage("Uploading...");
      setMessageType("info");

      const token = await getToken();

      const formData = new FormData();
      selectedFiles.forEach((file) => {
        formData.append("files", file);
      });

      await axios.post(apiEndpoints.UPLOAD_FILE, formData, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "multipart/form-data",
        },
      });

      setMessage("Files uploaded successfully!");
      setMessageType("success");
      setSelectedFiles([]);

      await fetchRecentFiles();
    } catch (error) {
      console.error("Upload error:", error);
      setMessage("Upload failed. Try again.");
      setMessageType("error");
    } finally {
      setUploading(false);
    }
  };

  return (
    <DashboardLayout activeMenu="Dashboard">
      <div className="p-6">
        <h1 className="text-2xl font-bold mb-6">My Drive</h1>

        {message && (
          <div
            className={`mb-6 p-4 rounded ${
              messageType === "error"
                ? "bg-red-50 text-red-700"
                : messageType === "success"
                ? "bg-green-50 text-green-700"
                : "bg-blue-50 text-blue-700"
            }`}
          >
            {message}
          </div>
        )}

        <div className="flex flex-col md:flex-row gap-6">
          <div className="w-full md:w-[40%]">
            <DashboardUpload
              files={selectedFiles}
              onFileChange={handleFileChange}
              onUpload={handleUpload}
              uploading={uploading}
              onRemoveFile={handleRemoveFile}
              remainingUploads={MAX_FILES - selectedFiles.length}
            />
          </div>

          <div className="w-full md:w-[60%]">
            {loading ? (
              <div className="bg-white p-8 flex items-center justify-center">
                <Loader2 className="animate-spin text-purple-500" size={40} />
              </div>
            ) : (
              <RecentFiles files={recentFiles} />
            )}
          </div>
        </div>
      </div>
    </DashboardLayout>
  );
};

export default Dashboard;
