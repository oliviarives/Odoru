import React, { useMemo, useState } from "react";

const API = "http://localhost:9000";

const demoAccounts = [
  { label: "President", username: "president", password: "pres123" },
  { label: "Secretaire", username: "secretaire", password: "secret123" },
  { label: "Prof", username: "prof", password: "prof123" },
  { label: "Eleve", username: "eleve", password: "eleve123" }
];

const initialCourse = {
  titre: "Initiation rythmique",
  niveauCible: 2,
  jour: "MONDAY",
  heureDebut: "18:30",
  lieu: "Salle CallMe",
  dureeMinutes: 60,
  dateDebut: "2026-06-22"
};

function asPrettyJson(value) {
  if (value == null || value === "") {
    return "";
  }
  return JSON.stringify(value, null, 2);
}

function formatError(status, body) {
  if (status === 401) {
    return "401 - authentification requise ou token invalide.";
  }
  if (status === 403) {
    return "403 - role insuffisant pour cette action.";
  }
  if (typeof body === "string") {
    return `${status} - ${body}`;
  }
  return `${status} - ${body?.message || body?.error || "requete refusee"}`;
}

async function readResponse(response) {
  const text = await response.text();
  if (!text) {
    return null;
  }
  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
}

function App() {
  const [session, setSession] = useState(() => {
    const saved = localStorage.getItem("odoru-session");
    return saved ? JSON.parse(saved) : null;
  });
  const [credentials, setCredentials] = useState({
    username: "president",
    password: "pres123"
  });
  const [loading, setLoading] = useState(false);
  const [status, setStatus] = useState("");
  const [result, setResult] = useState(null);
  const [course, setCourse] = useState(initialCourse);
  const [memberId, setMemberId] = useState("4");
  const [niveau, setNiveau] = useState("2");

  const authHeaders = useMemo(() => {
    if (!session?.token) {
      return {};
    }
    return { Authorization: `Bearer ${session.token}` };
  }, [session]);

  async function request(path, options = {}) {
    setLoading(true);
    setStatus("Appel en cours...");
    try {
      const response = await fetch(`${API}${path}`, {
        ...options,
        headers: {
          "Content-Type": "application/json",
          ...authHeaders,
          ...options.headers
        }
      });
      const body = await readResponse(response);
      if (!response.ok) {
        setStatus(formatError(response.status, body));
        setResult(body);
        return null;
      }
      setStatus(`${response.status} - succes`);
      setResult(body);
      return body;
    } catch (error) {
      setStatus("Impossible de joindre le backend sur http://localhost:9000.");
      setResult({ message: error.message });
      return null;
    } finally {
      setLoading(false);
    }
  }

  async function login(event) {
    event.preventDefault();
    const body = await request("/api/auth/login", {
      method: "POST",
      headers: {},
      body: JSON.stringify(credentials)
    });
    if (body?.token) {
      localStorage.setItem("odoru-session", JSON.stringify(body));
      setSession(body);
      setStatus(`Connecte en ${body.role}`);
    }
  }

  function logout() {
    localStorage.removeItem("odoru-session");
    setSession(null);
    setResult(null);
    setStatus("Session fermee.");
  }

  function pickAccount(account) {
    setCredentials({
      username: account.username,
      password: account.password
    });
  }

  function updateCourse(field, value) {
    setCourse((current) => ({
      ...current,
      [field]: field === "niveauCible" || field === "dureeMinutes" ? Number(value) : value
    }));
  }

  return (
    <main className="app-shell">
      <section className="topbar">
        <div>
          <p className="eyebrow">Club CallMe</p>
          <h1>Odoru</h1>
        </div>
        {session && (
          <div className="session-pill">
            <span>{session.role}</span>
            <button type="button" onClick={logout}>Deconnexion</button>
          </div>
        )}
      </section>

      <section className="workspace">
        <aside className="panel login-panel">
          <h2>Connexion</h2>
          <form onSubmit={login} className="stack">
            <label>
              Identifiant
              <input
                value={credentials.username}
                onChange={(event) =>
                  setCredentials((current) => ({ ...current, username: event.target.value }))
                }
              />
            </label>
            <label>
              Mot de passe
              <input
                type="password"
                value={credentials.password}
                onChange={(event) =>
                  setCredentials((current) => ({ ...current, password: event.target.value }))
                }
              />
            </label>
            <button className="primary" type="submit" disabled={loading}>
              Se connecter
            </button>
          </form>

          <div className="account-grid" aria-label="Comptes de demonstration">
            {demoAccounts.map((account) => (
              <button key={account.username} type="button" onClick={() => pickAccount(account)}>
                {account.label}
              </button>
            ))}
          </div>
        </aside>

        <section className="panel actions-panel">
          <div className="section-title">
            <h2>Actions demo</h2>
            {session ? <span>ID membre {session.membreId}</span> : <span>Non connecte</span>}
          </div>

          <div className="action-grid">
            <button type="button" onClick={() => request(`/api/membres/${session?.membreId || 4}`)}>
              Mes infos
            </button>
            <button type="button" onClick={() => request("/api/membres")}>
              Liste membres
            </button>
            <button type="button" onClick={() => request("/api/stats/cours")}>
              Stats cours
            </button>
            <button type="button" onClick={() => request(`/api/membres/${memberId}/historique`)}>
              Historique eleve
            </button>
          </div>

          <div className="split">
            <form
              className="stack compact"
              onSubmit={(event) => {
                event.preventDefault();
                request("/api/cours", {
                  method: "POST",
                  body: JSON.stringify(course)
                });
              }}
            >
              <h3>Planifier un cours</h3>
              <input
                value={course.titre}
                onChange={(event) => updateCourse("titre", event.target.value)}
                aria-label="Titre"
              />
              <div className="row">
                <input
                  type="number"
                  min="1"
                  max="5"
                  value={course.niveauCible}
                  onChange={(event) => updateCourse("niveauCible", event.target.value)}
                  aria-label="Niveau"
                />
                <select
                  value={course.jour}
                  onChange={(event) => updateCourse("jour", event.target.value)}
                  aria-label="Jour"
                >
                  <option value="MONDAY">Lundi</option>
                  <option value="TUESDAY">Mardi</option>
                  <option value="WEDNESDAY">Mercredi</option>
                  <option value="THURSDAY">Jeudi</option>
                  <option value="FRIDAY">Vendredi</option>
                  <option value="SATURDAY">Samedi</option>
                  <option value="SUNDAY">Dimanche</option>
                </select>
              </div>
              <div className="row">
                <input
                  type="time"
                  value={course.heureDebut}
                  onChange={(event) => updateCourse("heureDebut", event.target.value)}
                  aria-label="Heure"
                />
                <input
                  type="number"
                  min="15"
                  step="15"
                  value={course.dureeMinutes}
                  onChange={(event) => updateCourse("dureeMinutes", event.target.value)}
                  aria-label="Duree"
                />
              </div>
              <input
                value={course.lieu}
                onChange={(event) => updateCourse("lieu", event.target.value)}
                aria-label="Lieu"
              />
              <input
                type="date"
                value={course.dateDebut}
                onChange={(event) => updateCourse("dateDebut", event.target.value)}
                aria-label="Date"
              />
              <button className="primary" type="submit">Creer le cours</button>
            </form>

            <form
              className="stack compact"
              onSubmit={(event) => {
                event.preventDefault();
                request(`/api/stats/competitions?niveauCible=${niveau}`);
              }}
            >
              <h3>Parametres rapides</h3>
              <label>
                Eleve cible
                <input value={memberId} onChange={(event) => setMemberId(event.target.value)} />
              </label>
              <label>
                Niveau competition
                <input value={niveau} onChange={(event) => setNiveau(event.target.value)} />
              </label>
              <button type="submit">Stats competitions</button>
              <button type="button" onClick={() => request(`/api/membres/${memberId}/competitions/resultats`)}>
                Resultats eleve
              </button>
            </form>
          </div>
        </section>

        <section className="panel response-panel">
          <div className="section-title">
            <h2>Reponse API</h2>
            <span>{loading ? "Chargement" : status || "En attente"}</span>
          </div>
          <pre>{asPrettyJson(result) || "Connecte-toi puis lance une action."}</pre>
        </section>
      </section>
    </main>
  );
}

export default App;
