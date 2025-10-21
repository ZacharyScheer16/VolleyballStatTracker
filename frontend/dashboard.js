const API_PROFILE_URL = 'http://localhost:8080/api/user/profile';
const API_PASSWORD_URL = 'http://localhost:8080/api/user/password'; // New API endpoint
let currentUser = null;

document.addEventListener('DOMContentLoaded', function() {
    const token = localStorage.getItem('jwtToken');
    const welcomeHeading = document.querySelector('.welcome-card h1');
    // Using a more reliable selector (like an ID if you add one) is recommended, but keeping your original selector for now:
    const profileLink = document.querySelector('.nav-links a:last-child');

    // --- Essential Security Check ---
    if (!token) {
        console.error('No JWT token found. Redirecting to login.');
        window.location.href = 'index.html';
        return;
    }

    // --- Setup Logout Button ---
    document.getElementById('logoutButton').addEventListener('click', function() {
        localStorage.removeItem('jwtToken');
        window.location.href = 'index.html';
    });

    // --- Fetch Secured Profile Data (Runs on page load) ---
    fetch(API_PROFILE_URL, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
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
            currentUser = user;

            // 1. Personalize the Welcome Message
            welcomeHeading.textContent = `Welcome Back, ${user.email}!`;

            // 2. Make the "User Profile" link functional
            profileLink.addEventListener('click', function(event) {
                event.preventDefault();
                displayProfileDetails();
            });

        })
        .catch(error => {
            console.error('Error loading profile:', error);
            welcomeHeading.textContent = 'Error loading profile data.';
        });
});

/**
 * Renders the user's profile details and the Password Change Form.
 */
function displayProfileDetails() {
    if (!currentUser) return;

    const dashboardMain = document.querySelector('.dashboard-main');

    // Clear existing content and display the combined profile/password card
    dashboardMain.innerHTML = `
        <div class="welcome-card" style="text-align: left;">
            <h1>User Profile</h1>
            <div style="padding-bottom: 20px; border-bottom: 1px solid var(--color-text-muted);">
                <p><strong>Email:</strong> ${currentUser.email}</p>
                <p><strong>Role:</strong> COACH</p>
                <p><strong>User ID:</strong> ${currentUser.id}</p>
            </div>
            
            <h2 style="margin-top: 40px; color: var(--color-primary-blue); font-size: 1.8em;">
                Change Password
            </h2>

            <form id="passwordChangeForm" style="margin-top: 20px;">
                <div class="form-group">
                    <label for="currentPassword">Current Password</label>
                    <input type="password" id="currentPassword" name="currentPassword" required>
                </div>
                <div class="form-group">
                    <label for="newPassword">New Password (min 8 chars)</label>
                    <input type="password" id="newPassword" name="newPassword" required minlength="8">
                </div>
                
                <button type="submit" id="updatePasswordButton" style="width: 100%; margin-top: 15px; font-size: 1.2em; padding: 15px;">
                    Update Password
                </button>
                <p id="passwordMessage" class="error-message" style="margin-top: 15px;"></p>
            </form>
        </div>
        
        <button id="logoutButton" style="width: auto; padding: 10px 20px; margin-top: 30px;">
            Log Out
        </button>
    `;

    // CRITICAL: Re-attach the form submission handler after the HTML has been replaced
    document.getElementById('passwordChangeForm').addEventListener('submit', handlePasswordChange);

    // Re-attach the logout button listener since the dashboardMain content was replaced
    document.getElementById('logoutButton').addEventListener('click', function() {
        localStorage.removeItem('jwtToken');
        window.location.href = 'index.html';
    });
}

/**
 * Handles the submission of the password change form, sending a secure PATCH request.
 */
async function handlePasswordChange(event) {
    event.preventDefault();

    const currentPassword = document.getElementById('currentPassword').value;
    const newPassword = document.getElementById('newPassword').value;
    const messageElement = document.getElementById('passwordMessage');
    const token = localStorage.getItem('jwtToken');

    messageElement.textContent = '';
    messageElement.className = 'error-message';

    if (newPassword.length < 8) {
        messageElement.textContent = 'New password must be at least 8 characters long.';
        return;
    }

    try {
        const response = await fetch(API_PASSWORD_URL, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` // Include the JWT for authorization
            },
            body: JSON.stringify({
                currentPassword: currentPassword,
                newPassword: newPassword
            })
        });

        const text = await response.text(); // Read as text since success and failure bodies are strings

        if (response.ok) {
            // Success
            messageElement.textContent = 'Password updated successfully! Please log in again with the new password.';
            messageElement.className = 'success-message';

            // Clear form fields
            document.getElementById('currentPassword').value = '';
            document.getElementById('newPassword').value = '';

            // Optional: Force user to log in again after changing password
            setTimeout(() => {
                localStorage.removeItem('jwtToken');
                window.location.href = 'index.html';
            }, 3000);

        } else {
            // Failure (400 Bad Request, 500 Internal Error, etc.)
            // The backend returns the error message in the body, so we display it.
            messageElement.textContent = text || 'Password update failed. Check your current password.';
        }

    } catch (error) {
        console.error('Network Error during password change:', error);
        messageElement.textContent = 'Could not connect to the server or a network error occurred.';
    }
}