const FileCard = ({ file, onDelete, onTogglePublic, onDownload, onShareLink }) => {

  const safeName = file.fileName || "unknown";
  const ext = safeName.includes(".") ? safeName.split(".").pop().toLowerCase() : "";

  const formatSize = (size = 0) => {
    if (size < 1024) return `${size} B`;
    if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`;
    return `${(size / 1024 / 1024).toFixed(1)} MB`;
  };

  return (
    <div className="relative rounded-lg bg-white shadow p-4">
      <h3 className="truncate font-medium">{safeName}</h3>
      <p className="text-xs text-gray-500">
        {formatSize(file.fileSize)} • {new Date(file.uploadedAt).toLocaleDateString()}
      </p>

      <div className="flex gap-2 mt-3">
        <button onClick={() => onDownload(file)}>⬇</button>
        <button onClick={() => onTogglePublic(file)}>
          {file.isPublic ? "🔓" : "🔒"}
        </button>
        <button onClick={() => onDelete(file.id)}>🗑</button>
      </div>
    </div>
  );
};

export default FileCard;
