const Footer = () => {
    return (
        <footer className="bg-gray-800">
            <div className="max-w-7xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
                <div className="text-center">
                    <p className="text-base text-gray-400">
                        &copy; 2025 CloudShare. All rights reserved.
                    </p>

                    <p className="text-base text-white mt-2">
                        Developed by{" "}
                        <a
                            href="https://www.linkedin.com/in/gagan-pandey-4514b0289/"
                            target="_blank"
                            rel="noopener noreferrer"
                            className="text-blue-400 hover:text-blue-500 underline transition-colors duration-200"
                        >
                            Gagan Pandey
                        </a>
                    </p>
                </div>
            </div>
        </footer>
    );
};

export default Footer;
