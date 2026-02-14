import {
    Files,
    LayoutDashboard,
    Upload
} from "lucide-react";

/* ===========================
   HOME PAGE FEATURES
   =========================== */

export const features = [
    {
        iconName: "ArrowUpCircle",
        iconColor: "text-purple-500",
        title: "Unlimited File Upload",
        description: "Upload as many files as you want with no limits or restrictions."
    },
    {
        iconName: "Shield",
        iconColor: "text-green-500",
        title: "Secure Storage",
        description: "Your files are encrypted and stored securely in the cloud."
    },
    {
        iconName: "Share2",
        iconColor: "text-purple-500",
        title: "Simple Sharing",
        description: "Share files easily with secure links you control."
    },
    {
        iconName: "FileText",
        iconColor: "text-red-500",
        title: "File Management",
        description: "View, organize, and manage all your files in one place."
    },
    {
        iconName: "Clock",
        iconColor: "text-indigo-500",
        title: "Instant Access",
        description: "Access your files anytime, anywhere from any device."
    }
];

/* ===========================
   PRICING & CREDITS REMOVED
   =========================== */
// ❌ pricingPlans removed
// ❌ subscriptions removed
// ❌ credits removed
// ❌ payments removed

/* ===========================
   TESTIMONIALS
   =========================== */

export const testimonials = [
    {
        name: "Sarah Johnson",
        role: "Marketing Director",
        company: "CreativeMinds Inc.",
        image: "https://randomuser.me/api/portraits/women/32.jpg",
        quote: "CloudShare has made file sharing incredibly easy for our team. The interface is clean and fast.",
        rating: 5
    },
    {
        name: "Michael Chen",
        role: "Freelance Designer",
        company: "Self-employed",
        image: "https://randomuser.me/api/portraits/men/46.jpg",
        quote: "I love the unlimited uploads. No more worrying about limits or payments.",
        rating: 5
    },
    {
        name: "Priya Sharma",
        role: "Project Manager",
        company: "TechSolutions Ltd.",
        image: "https://randomuser.me/api/portraits/women/65.jpg",
        quote: "CloudShare keeps all our project files organized and accessible.",
        rating: 4
    }
];

/* ===========================
   SIDE MENU (NO PAYMENTS)
   =========================== */

export const SIDE_MENU_DATA = [
    {
        id: "01",
        label: "Dashboard",
        icon: LayoutDashboard,
        path: "/dashboard",
    },
    {
        id: "02",
        label: "Upload",
        icon: Upload,
        path: "/upload",
    },
    {
        id: "03",
        label: "My Files",
        icon: Files,
        path: "/my-files",
    }
];

// ❌ Subscription menu removed
// ❌ Transactions menu removed
// ❌ Credits display removed
