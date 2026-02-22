import { FileIcon, FileText, Music, Video, Image } from "lucide-react";

// ✅ Always returns a valid filename string
export const getSafeFileName = (file) => {
  if (!file) return "Unknown file";
  return file.name || "Unknown file";
};

// ✅ Never crashes - handles all edge cases
export const getFileIcon = (fileName) => {
  if (!fileName || typeof fileName !== "string") {
    return <FileIcon size={18} className="text-blue-600" />;
  }

  const extension = fileName.includes(".")
    ? fileName.split(".").pop().toLowerCase()
    : "";

  if (["jpg", "jpeg", "png", "gif", "svg", "webp"].includes(extension))
    return <Image size={18} className="text-purple-500" />;

  if (["mp4", "webm", "mov", "avi", "mkv"].includes(extension))
    return <Video size={18} className="text-blue-500" />;

  if (["mp3", "wav", "ogg", "flac", "m4a"].includes(extension))
    return <Music size={18} className="text-green-500" />;

  if (["pdf", "doc", "docx", "txt", "rtf", "md"].includes(extension))
    return <FileText size={18} className="text-amber-500" />;

  return <FileIcon size={18} className="text-blue-600" />;
};