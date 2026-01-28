import { Link } from "react-router-dom";

export default function Register() {
  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center p-6">
      <div className="w-full max-w-sm bg-white border rounded p-6 space-y-4">
        <h1 className="text-xl font-bold">Register</h1>
        <p className="text-sm text-gray-600">
          Registration is planned for future development.
        </p>

        <Link
          to="/login"
          className="block text-center bg-gray-200 rounded py-2 text-sm font-medium"
        >
          Back to login
        </Link>
      </div>
    </div>
  );
}
