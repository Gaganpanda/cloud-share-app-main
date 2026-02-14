import { ArrowUpFromLine, X, FileIcon, Loader2 } from "lucide-react";
import { useRef } from "react";

const DashboardUpload = ({
  files,
  onFileChange,
  onUpload,
  uploading,
  onRemoveFile,
  remainingUploads,
}) => {
  const fileInputRef = useRef(null);

  const handleDragOver = (e) => {
    e.preventDefault();
  };

  const handleDrop = (e) => {
    e.preventDefault();
    const droppedFiles = Array.from(e.dataTransfer.files);
    if (droppedFiles.length > 0) {
      onFileChange({ target: { files: droppedFiles } });
    }
  };

  const handleBrowseClick = () => {
    fileInputRef.current.click();
  };

  const formatFileSize = (bytes) => {
    if (bytes < 1024) return bytes + " B";
    if (bytes < 1048576) return (bytes / 1024).toFixed(2) + " KB";
    return (bytes / 1048576).toFixed(2) + " MB";
  };

  return (
    <div className="w-full">
      <div className="flex items-center justify-between mb-2">
        <div className="flex items-center gap-2">
          <ArrowUpFromLine className="text-purple-500" size={18} />
          <h2 className="text-base font-medium">Upload Files</h2>
        </div>
        <div className="text-xs text-gray-500">
          {remainingUploads} of 5 files remaining
        </div>
      </div>

      <div
        className="border-dashed border-2 border-gray-300 rounded-lg p-6 text-center bg-white cursor-pointer hover:border-purple-500"
        onDragOver={handleDragOver}
        onDrop={handleDrop}
        onClick={handleBrowseClick}
      >
        <div className="flex flex-col items-center">
          <ArrowUpFromLine size={20} className="text-purple-500 mb-2" />
          <p className="text-sm">Drag & Drop files</p>
          <p className="text-xs text-gray-500">or click to browse</p>

          <input
            ref={fileInputRef}
            type="file"
            multiple
            className="hidden"
            onChange={(e) => {
              onFileChange(e);
              e.target.value = null;
            }}
          />
        </div>
      </div>

      {files.length > 0 && (
        <>
          <div className="mt-4 bg-white border rounded-lg">
            {files.map((file, index) => (
              <div
                key={index}
                className="flex justify-between items-center p-2 border-b last:border-b-0"
              >
                <div className="flex gap-2 items-center">
                  <FileIcon size={16} className="text-purple-500" />
                  <div>
                    <p className="text-xs font-medium">{file.name}</p>
                    <p className="text-xs text-gray-500">
                      {formatFileSize(file.size)}
                    </p>
                  </div>
                </div>

                <button
                  onClick={() => onRemoveFile(index)}
                  disabled={uploading}
                  className="text-gray-400 hover:text-red-500"
                >
                  <X size={16} />
                </button>
              </div>
            ))}
          </div>

          <button
            onClick={onUpload}
            disabled={uploading}
            className="mt-3 w-full py-2 bg-purple-500 text-white rounded-md hover:bg-purple-600 disabled:opacity-50 flex items-center justify-center gap-2"
          >
            {uploading ? (
              <>
                <Loader2 size={16} className="animate-spin" />
                Uploading...
              </>
            ) : (
              `Upload ${files.length} File(s)`
            )}
          </button>
        </>
      )}
    </div>
  );
};

export default DashboardUpload;
