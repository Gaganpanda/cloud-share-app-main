import {
  Copy,
  Download,
  Eye,
  Globe,
  Lock,
  Trash2
} from "lucide-react";
import { getFileIcon, getSafeFileName } from "../utils/fileUtils";

const FileListRow = ({
  file,
  onDownload,
  onDelete,
  onTogglePublic,
  onShareLink
}) => {
  const fileName = getSafeFileName(file);

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
        {(file.size / 1024).toFixed(1)} KB
      </td>

      <td className="px-6 py-4 text-sm text-gray-600">
        {new Date(file.uploadedAt).toLocaleDateString()}
      </td>

      <td className="px-6 py-4 text-sm text-gray-600">
        <div className="flex items-center gap-4">
          <button
            onClick={() => onTogglePublic(file)}
            className="flex items-center gap-2 group"
          >
            {file.isPublic ? (
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

          {file.isPublic && (
            <button
              onClick={() => onShareLink(file.id)}
              className="flex items-center gap-2 text-blue-600 group"
            >
              <Copy size={16} />
              <span className="group-hover:underline">Share</span>
            </button>
          )}
        </div>
      </td>

      <td className="px-6 py-4">
        <div className="grid grid-cols-3 gap-4">
          <button onClick={() => onDownload(file)}>
            <Download size={18} />
          </button>

          <button onClick={() => onDelete(file.id)}>
            <Trash2 size={18} />
          </button>

          {file.isPublic ? (
            <a href={`/file/${file.id}`} target="_blank" rel="noreferrer">
              <Eye size={18} />
            </a>
          ) : (
            <span />
          )}
        </div>
      </td>
    </tr>
  );
};

export default FileListRow;
