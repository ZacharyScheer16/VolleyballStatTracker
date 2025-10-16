document.getElementById('loginForm').addEventListener('submit', async function(event) {
    event.preventDefault(); // Prevent the default form submission

    // Note: We use the variable 'username' here because that's the ID of the input field
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const messageElement = document.getElementById('message');

    // Clear previous messages
    messageElement.textContent = '';
    messageElement.className = 'error-message';

    // 1. Send Credentials to Spring Boot Backend
    try {
        const response = await fetch('http://localhost:8080/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                // CRITICAL FIX: The backend DTO expects 'email', not 'username'.
                email: username,
                password: password
            })
        });

        const data = await response.json();

        if (response.ok) {
            // 2. Success: Store the JWT Token
            const token = data.token;
            localStorage.setItem('jwtToken', token);

            // 3. SUCCESS REDIRECT: Send the user to the main dashboard page
            window.location.href = 'dashboard.html';

        } else {
            // 4. Failure: Display Error Message
            messageElement.textContent = data.message || 'Login failed. Check credentials.';
        }

    } catch (error) {
        console.error('Network Error:', error);
        messageElement.textContent = 'Could not connect to the server. Is the Docker backend running?';
    }
});
// DEMO CREDENTIALS: testuser@volleyball.com / password
