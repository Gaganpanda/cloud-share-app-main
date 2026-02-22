const FileCard = ({ file, onDelete, onTogglePublic, onDownload, onShareLink }) => {
  const safeName = file?.name || "Unknown file";

  const formatSize = (size = 0) => {
    if (!size || size === 0) return "0 KB";
    if (size < 1024) return `${size} B`;
    if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`;
    return `${(size / 1024 / 1024).toFixed(1)} MB`;
  };

  return (
    <div className="relative rounded-lg bg-white shadow p-4 hover:shadow-md transition">
      <h3 className="truncate font-medium text-gray-800">{safeName}</h3>
      <p className="text-xs text-gray-500 mt-1">
        {formatSize(file?.size)} • {file?.uploadedAt ? new Date(file.uploadedAt).toLocaleDateString() : '-'}
      </p>

      <div className="flex gap-2 mt-4">
        <button
          onClick={() => onDownload(file)}
          className="px-3 py-1 text-sm bg-purple-50 text-purple-600 rounded hover:bg-purple-100"
          title="Download"
        >
          ⬇ Download
        </button>
        <button
          onClick={() => onTogglePublic(file)}
          className="px-3 py-1 text-sm bg-gray-50 text-gray-600 rounded hover:bg-gray-100"
          title={file?.publicStatus ? "Make Private" : "Make Public"}
        >
          {file?.publicStatus ? "🔓 Public" : "🔒 Private"}
        </button>
        <button
          onClick={() => onDelete(file.id)}
          className="px-3 py-1 text-sm bg-red-50 text-red-600 rounded hover:bg-red-100"
          title="Delete"
        >
          🗑 Delete
        </button>
      </div>
    </div>
  );
};

export default FileCard;