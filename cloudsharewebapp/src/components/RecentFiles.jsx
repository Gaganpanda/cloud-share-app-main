import {
    FileIcon,
    FileText,
    Music,
    Video,
    Image,
    Globe,
    Lock
} from "lucide-react";

const RecentFiles = ({ files = [] }) => {

    const getFileIcon = (fileName) => {
        if (!fileName || typeof fileName !== "string") {
            return <FileIcon size={18} className="text-blue-600" />;
        }

        const extension = fileName.includes(".")
            ? fileName.split(".").pop().toLowerCase()
            : "";

        if (["jpg", "jpeg", "png", "gif", "svg", "webp"].includes(extension)) {
            return <Image size={18} className="text-purple-500" />;
        }

        if (["mp4", "webm", "mov", "avi", "mkv"].includes(extension)) {
            return <Video size={18} className="text-blue-500" />;
        }

        if (["mp3", "wav", "ogg", "flac", "m4a"].includes(extension)) {
            return <Music size={18} className="text-green-500" />;
        }

        if (["pdf", "doc", "docx", "txt", "rtf", "md"].includes(extension)) {
            return <FileText size={18} className="text-amber-500" />;
        }

        return <FileIcon size={18} className="text-blue-600" />;
    };

    const formatFileSize = (bytes = 0) => {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1048576) return (bytes / 1024).toFixed(1) + " KB";
        return (bytes / 1048576).toFixed(1) + " MB";
    };

    const formatDate = (dateString) => {
        if (!dateString) return "-";
        return new Date(dateString).toLocaleDateString();
    };

    return (
        <div className="bg-white rounded-lg shadow overflow-hidden">
            <table className="min-w-full">
                <thead className="bg-gray-50 text-xs uppercase text-gray-500">
                    <tr>
                        <th className="px-4 py-3 text-left">Name</th>
                        <th className="px-4 py-3 text-left">Size</th>
                        <th className="px-4 py-3 text-left">Uploaded</th>
                        <th className="px-4 py-3 text-left">Sharing</th>
                    </tr>
                </thead>

                <tbody className="divide-y">
                    {files.length === 0 && (
                        <tr>
                            <td colSpan={4} className="py-8 text-center text-gray-400">
                                No files uploaded yet
                            </td>
                        </tr>
                    )}

                    {files.map((file) => {
                        const fileName =
                            file.originalName ||
                            file.fileName ||
                            file.filename ||
                            file.name ||
                            "Unknown file";

                        return (
                            <tr key={file.id}>
                                <td className="px-4 py-3 flex items-center gap-2">
                                    {getFileIcon(fileName)}
                                    <span className="truncate">{fileName}</span>
                                </td>

                                <td className="px-4 py-3 text-sm text-gray-600">
                                    {formatFileSize(file.size)}
                                </td>

                                <td className="px-4 py-3 text-sm text-gray-600">
                                    {formatDate(file.uploadedAt)}
                                </td>

                                <td className="px-4 py-3">
                                    {file.public ? (
                                        <span className="flex items-center text-green-600 text-xs">
                                            <Globe size={14} className="mr-1" /> Public
                                        </span>
                                    ) : (
                                        <span className="flex items-center text-gray-500 text-xs">
                                            <Lock size={14} className="mr-1" /> Private
                                        </span>
                                    )}
                                </td>
                            </tr>
                        );
                    })}
                </tbody>
            </table>
        </div>
    );
};

export default RecentFiles;
