const API_PROFILE_URL = 'http://localhost:8080/api/user/profile';
let currentUser = null; // Variable to store the fetched user object

document.addEventListener('DOMContentLoaded', function() {
    const token = localStorage.getItem('jwtToken');
    const welcomeHeading = document.querySelector('.welcome-card h1');
    const profileLink = document.querySelector('.nav-links a:last-child'); // Targeting the "User Profile" link

    // --- Essential Security Check ---
    if (!token) {
        console.error('No JWT token found. Redirecting to login.');
        window.location.href = 'index.html'; // Kick unauthenticated users out
        return;
    }

    // --- Setup Logout Button ---
    document.getElementById('logoutButton').addEventListener('click', function() {
        localStorage.removeItem('jwtToken'); // Clear the token
        window.location.href = 'index.html'; // Redirect to the login page
    });

    // --- Fetch Secured Profile Data (Runs on page load) ---
    fetch(API_PROFILE_URL, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`, // Include the JWT
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            }

            // If 401/403 (token expired or invalid), clear token and redirect
            localStorage.removeItem('jwtToken');
            window.location.href = 'index.html';
            throw new Error('Session expired or invalid. Please sign in.');
        })
        .then(user => {
            currentUser = user; // Save user data globally

            // 1. Personalize the Welcome Message
            welcomeHeading.textContent = `Welcome Back, ${user.email}!`;

            // 2. Make the "User Profile" link functional (for this demo)
            profileLink.addEventListener('click', function(event) {
                event.preventDefault(); // Stop default navigation
                displayProfileDetails();
            });

        })
        .catch(error => {
            console.error('Error loading profile:', error);
            welcomeHeading.textContent = 'Error loading profile data.';
        });
});

/**
 * Function to display the user's profile details on the dashboard view.
 * In a real app, this would switch to a dedicated profile page or modal.
 */
function displayProfileDetails() {
    if (!currentUser) return; // Wait until data is loaded

    // Create a dynamic profile card (Minimal demonstration)
    const dashboardMain = document.querySelector('.dashboard-main');

    // Clear existing content and display profile details
    dashboardMain.innerHTML = `
        <div class="welcome-card" style="text-align: left;">
            <h1>User Profile</h1>
            <p><strong>Email:</strong> ${currentUser.email}</p>
            <p><strong>Role:</strong> ${currentUser.role}</p>
            <p><strong>User ID:</strong> ${currentUser.id}</p>
            <p style="margin-top: 20px;">
                <a href="#" onclick="alert('Ready for tomorrow! We will implement the password and detail update forms here.');">Edit Profile</a>
            </p>
        </div>
    `;
}