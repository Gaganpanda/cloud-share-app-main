import {useState} from "react";
import {Menu, Share2, X} from "lucide-react";
import {SignedIn, UserButton} from "@clerk/clerk-react";
import SideMenu from "./SideMenu.jsx";

const Navbar = ({activeMenu}) => {
    const [openSideMenu, setOpenSideMenu] = useState(false);

    return (
        <div className="flex items-center justify-between gap-5 bg-white border-b border-gray-200/50 py-4 px-4 sm:px-7 sticky top-0 z-30">

            {/* Left */}
            <div className="flex items-center gap-5">
                <button
                    onClick={() => setOpenSideMenu(!openSideMenu)}
                    className="block lg:hidden text-black hover:bg-gray-100 p-1 rounded">
                    {openSideMenu ? <X /> : <Menu />}
                </button>

                <div className="flex items-center gap-2">
                    <Share2 className="text-blue-600" />
                    <span className="text-lg font-medium">Cloud Share</span>
                </div>
            </div>

            {/* Right */}
            <SignedIn>
                <UserButton />
            </SignedIn>

            {/* Mobile Side Menu */}
            {openSideMenu && (
                <div className="fixed top-[73px] left-0 right-0 bg-white border-b lg:hidden z-20">
                    <SideMenu activeMenu={activeMenu}/>
                </div>
            )}
        </div>
    )
}

export default Navbar;
