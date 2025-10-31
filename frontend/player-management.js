/* V2.8: Player Management JS */
const API_PLAYER_URL = 'http://localhost:8080/api/player';
const API_PROFILE_URL = 'http://localhost:8080/api/user/profile';
const rosterBody = document.getElementById('rosterBody');
const playerFormContainer = document.getElementById('playerFormContainer');
const playerForm = document.getElementById('playerForm');
const formTitle = document.getElementById('formTitle');
const playerMessage = document.getElementById('playerMessage');
let currentEditingId = null;

// --- INITIALIZATION ---
document.addEventListener('DOMContentLoaded', function() {
    const token = localStorage.getItem('jwtToken');
    if (!token) {
        window.location.href = 'index.html'; // Redirect if not logged in
        return;
    }

    // Since the form is now in the HTML, we must ensure playerForm is found
    if (!playerForm) {
        console.error("Error: Player form element not found in the HTML.");
        return;
    }

    // Set up navigation and initial data load
    setupNavigation(token);
    loadRoster();

    // Setup form visibility toggle
    document.getElementById('addPlayerButton').addEventListener('click', () => {
        resetAndShowForm();
    });

    // Setup form submission for Create/Update
    playerForm.addEventListener('submit', handlePlayerSubmit);
});

function setupNavigation(token) {
    // Setup Logout Button
    document.getElementById('logoutButton').addEventListener('click', () => {
        localStorage.removeItem('jwtToken');
        window.location.href = 'index.html';
    });

    // Make the "User Profile" link functional
    document.getElementById('profileNav').addEventListener('click', (e) => {
        e.preventDefault();
        window.location.href = 'dashboard.html';
    });
}

// --- CRUD OPERATIONS ---

/**
 * Loads and renders the entire player roster (GET /api/player)
 */
async function loadRoster() {
    rosterBody.innerHTML = '<tr><td colspan="4" style="text-align: center; padding: 20px;">Loading roster...</td></tr>';
    try {
        const response = await fetch(API_PLAYER_URL, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            if (response.status === 401 || response.status === 403) {
                throw new Error('Session expired. Please log in again.');
            }
            throw new Error(`Failed to load roster: ${response.statusText}`);
        }

        const players = await response.json();
        renderRoster(players);

    } catch (error) {
        console.error('Error loading roster:', error);
        rosterBody.innerHTML = `<tr><td colspan="4" class="error-message" style="text-align: center; padding: 20px;">
                                    ${error.message}
                                </td></tr>`;
        if (error.message.includes('expired')) {
            setTimeout(() => {
                localStorage.removeItem('jwtToken');
                window.location.href = 'index.html';
            }, 1500);
        }
    }
}

/**
 * Handles form submission for both creating (POST) and updating (PUT) a player.
 */
async function handlePlayerSubmit(event) {
    event.preventDefault();
    playerMessage.textContent = '';
    playerMessage.className = 'error-message';

    const playerId = document.getElementById('playerId').value;
    const isUpdate = !!playerId;

    const requestBody = {
        name: document.getElementById('name').value,
        number: parseInt(document.getElementById('number').value),
        position: document.getElementById('position').value
    };

    const method = isUpdate ? 'PUT' : 'POST';
    const url = isUpdate ? `${API_PLAYER_URL}/${playerId}` : API_PLAYER_URL;

    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
            },
            body: JSON.stringify(requestBody)
        });

        const responseText = await response.text();

        if (response.ok) {
            // Success
            playerMessage.textContent = `Player ${isUpdate ? 'updated' : 'added'} successfully!`;
            playerMessage.className = 'success-message';

            setTimeout(() => {
                playerFormContainer.style.display = 'none';
                loadRoster();
            }, 1000);

        } else {
            // Failure
            playerMessage.textContent = responseText || `Operation failed. Status: ${response.status}`;
        }
    } catch (error) {
        console.error('Network Error:', error);
        playerMessage.textContent = 'Could not connect to the server or a network error occurred.';
    }
}

/**
 * Deletes a player (DELETE /api/player/{id})
 */
async function deletePlayer(playerId) {
    if (!confirm('Are you sure you want to delete this player?')) {
        return;
    }

    try {
        const response = await fetch(`${API_PLAYER_URL}/${playerId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
            }
        });

        if (response.ok) {
            alert('Player deleted successfully.');
            loadRoster(); // Refresh the table
        } else {
            const error = await response.text();
            alert(`Failed to delete player: ${error}`);
        }

    } catch (error) {
        console.error('Delete Network Error:', error);
        alert('Could not connect to the server.');
    }
}

// --- RENDERING AND UTILITIES ---

/**
 * Renders the players array into the roster table body.
 */
function renderRoster(players) {
    rosterBody.innerHTML = '';
    if (players.length === 0) {
        rosterBody.innerHTML = '<tr><td colspan="4" style="text-align: center; padding: 20px; color: var(--color-text-muted);">No players on the roster yet. Add one!</td></tr>';
        return;
    }

    players.sort((a, b) => a.number - b.number);

    players.forEach(player => {
        const row = rosterBody.insertRow();
        row.style.fontSize = '1.1em'; // Basic row style

        // Columns
        row.insertCell().textContent = player.number;

        const nameCell = row.insertCell();
        nameCell.textContent = player.name;
        nameCell.style.textAlign = 'left';

        row.insertCell().textContent = player.position;

        // Actions Column
        const actionsCell = row.insertCell();
        actionsCell.style.textAlign = 'right';

        // 🚀 FIX 2: Corrected inline styles for action buttons 🚀
        const editButton = document.createElement('button');
        editButton.textContent = 'Edit';
        editButton.style.cssText = 'background-color: #4169E1; color: white; border: none; padding: 8px 15px; border-radius: 8px; font-weight: normal; margin: 5px; cursor: pointer; font-size: 0.9em; width: auto; box-shadow: none;';
        editButton.onclick = () => showEditForm(player);

        const deleteButton = document.createElement('button');
        deleteButton.textContent = 'Delete';
        deleteButton.style.cssText = 'background-color: #CC3333; color: white; border: none; padding: 8px 15px; border-radius: 8px; font-weight: normal; margin: 5px; cursor: pointer; font-size: 0.9em; width: auto; box-shadow: none;';
        deleteButton.onclick = () => deletePlayer(player.id);

        actionsCell.appendChild(editButton);
        actionsCell.appendChild(deleteButton);
    });
}

/**
 * Resets and shows the form for adding a new player.
 */
function resetAndShowForm() {
    playerForm.reset();
    document.getElementById('playerId').value = '';
    formTitle.textContent = 'Add New Player';
    playerMessage.textContent = '';
    playerFormContainer.style.display = 'block';
}

/**
 * Populates and shows the form for editing an existing player.
 */
function showEditForm(player) {
    document.getElementById('playerId').value = player.id;
    document.getElementById('name').value = player.name;
    document.getElementById('number').value = player.number;
    document.getElementById('position').value = player.position;
    formTitle.textContent = `Edit Player: ${player.name}`;
    playerMessage.textContent = '';
    playerFormContainer.style.display = 'block';
}