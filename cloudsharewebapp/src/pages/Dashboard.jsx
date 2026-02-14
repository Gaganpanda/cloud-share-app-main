import DashboardLayout from "../layout/DashboardLayout.jsx";
import { useAuth } from "@clerk/clerk-react";
import { useEffect, useState } from "react";
import axios from "axios";
import { apiEndpoints } from "../util/apiEndpoints.js";
import { Loader2 } from "lucide-react";
import DashboardUpload from "../components/DashboardUpload.jsx";
import RecentFiles from "../components/RecentFiles.jsx";

const Dashboard = () => {
  const [files, setFiles] = useState([]);
  const [uploadFiles, setUploadFiles] = useState([]);
  const [uploading, setUploading] = useState(false);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [messageType, setMessageType] = useState("");

  const { getToken } = useAuth();
  const MAX_FILES = 5;

  useEffect(() => {
    const fetchRecentFiles = async () => {
      setLoading(true);
      try {
        const token = await getToken();
        const res = await axios.get(apiEndpoints.FETCH_FILES, {
          headers: { Authorization: `Bearer ${token}` },
        });

        const sorted = res.data
          .sort((a, b) => new Date(b.uploadedAt) - new Date(a.uploadedAt))
          .slice(0, 5);

        setFiles(sorted);
      } catch (e) {
        console.error("Error fetching files", e);
      } finally {
        setLoading(false);
      }
    };

    fetchRecentFiles();
  }, [getToken]);

  const handleFileChange = (e) => {
    const selected = Array.from(e.target.files);

    if (uploadFiles.length + selected.length > MAX_FILES) {
      setMessage(`You can upload max ${MAX_FILES} files at once.`);
      setMessageType("error");
      return;
    }

    setUploadFiles((prev) => [...prev, ...selected]);
    setMessage("");
  };

  const handleRemoveFile = (index) => {
    setUploadFiles((prev) => prev.filter((_, i) => i !== index));
  };

  const handleUpload = async () => {
    if (uploadFiles.length === 0) {
      setMessage("Select at least one file.");
      setMessageType("error");
      return;
    }

    setUploading(true);
    setMessage("Uploading files...");
    setMessageType("info");

    const formData = new FormData();
    uploadFiles.forEach((f) => formData.append("files", f));

    try {
      const token = await getToken();
      await axios.post(apiEndpoints.UPLOAD_FILE, formData, {
        headers: { Authorization: `Bearer ${token}` },
      });

      setMessage("Files uploaded successfully!");
      setMessageType("success");
      setUploadFiles([]);

      // Refresh recent files
      const res = await axios.get(apiEndpoints.FETCH_FILES, {
        headers: { Authorization: `Bearer ${token}` },
      });

      setFiles(
        res.data
          .sort((a, b) => new Date(b.uploadedAt) - new Date(a.uploadedAt))
          .slice(0, 5)
      );
    } catch (e) {
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
                : "bg-green-50 text-green-700"
            }`}
          >
            {message}
          </div>
        )}

        <div className="flex flex-col md:flex-row gap-6">
          <div className="w-full md:w-[40%]">
            <DashboardUpload
              files={uploadFiles}
              onFileChange={handleFileChange}
              onUpload={handleUpload}
              uploading={uploading}
              onRemoveFile={handleRemoveFile}
            />
          </div>

          <div className="w-full md:w-[60%]">
            {loading ? (
              <div className="bg-white p-8 flex items-center justify-center">
                <Loader2 className="animate-spin text-purple-500" size={40} />
              </div>
            ) : (
              <RecentFiles files={files} />
            )}
          </div>
        </div>
      </div>
    </DashboardLayout>
  );
};

export default Dashboard;
