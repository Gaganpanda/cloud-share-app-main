import {
  Copy,
  Download,
  Eye,
  Globe,
  Lock,
  Trash2
} from "lucide-react";
import { getFileIcon } from "../util/fileUtils";

const FileListRow = ({
  file,
  onDownload,
  onDelete,
  onTogglePublic,
  onShareLink
}) => {
  // ✅ Use file.name directly from backend
  const fileName = file?.name || "Unknown file";

  // ✅ Format file size properly
  const formatSize = (bytes) => {
    if (!bytes || bytes === 0) return "0 KB";
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / 1024 / 1024).toFixed(1)} MB`;
  };

  return (
    <tr className="hover:bg-gray-50 transition-colors">
      <td className="px-6 py-4 text-sm font-medium text-gray-800">
        <div className="flex items-center gap-2">
          {getFileIcon(fileName)}
          <span className="truncate max-w-[200px]" title={fileName}>
            {fileName}
          </span>
        </div>
      </td>

      <td className="px-6 py-4 text-sm text-gray-600">
        {formatSize(file?.size)}
      </td>

      <td className="px-6 py-4 text-sm text-gray-600">
        {file?.uploadedAt ? new Date(file.uploadedAt).toLocaleDateString() : '-'}
      </td>

      <td className="px-6 py-4 text-sm text-gray-600">
        <div className="flex items-center gap-4">
          <button
            onClick={() => onTogglePublic(file)}
            className="flex items-center gap-2 group"
          >
            {file?.publicStatus ? (
              <>
                <Globe size={16} className="text-green-500" />
                <span className="group-hover:underline">Public</span>
              </>
            ) : (
              <>
                <Lock size={16} className="text-gray-500" />
                <span className="group-hover:underline">Private</span>
              </>
            )}
          </button>

          {file?.publicStatus && (
            <button
              onClick={() => onShareLink(file.id)}
              className="flex items-center gap-2 text-purple-600 group"
            >
              <Copy size={16} />
              <span className="group-hover:underline">Share</span>
            </button>
          )}
        </div>
      </td>

      <td className="px-6 py-4">
        <div className="flex items-center gap-3">
          <button
            onClick={() => onDownload(file)}
            className="text-purple-600 hover:text-purple-800"
            title="Download"
          >
            <Download size={18} />
          </button>

          <button
            onClick={() => onDelete(file.id)}
            className="text-red-600 hover:text-red-800"
            title="Delete"
          >
            <Trash2 size={18} />
          </button>

          {file?.publicStatus && (
            <a
              href={`/file/${file.id}`}
              target="_blank"
              rel="noreferrer"
              className="text-blue-600 hover:text-blue-800"
              title="View Public Link"
            >
              <Eye size={18} />
            </a>
          )}
        </div>
      </td>
    </tr>
  );
};

export default FileListRow;