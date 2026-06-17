import React, { useMemo, useState } from "react";

const API = `http://${window.location.hostname}:9000`;

const demoAccounts = [
  { label: "President", username: "president", password: "pres123" },
  { label: "Secretaire", username: "secretaire", password: "secret123" },
  { label: "Prof", username: "prof", password: "prof123" },
  { label: "Eleve", username: "eleve", password: "eleve123" }
];

const roleLabels = {
  PRESIDENT: "President",
  SECRETAIRE: "Secretariat",
  ENSEIGNANT: "Enseignant",
  ELEVE: "Eleve"
};

const dayLabels = {
  MONDAY: "Lundi",
  TUESDAY: "Mardi",
  WEDNESDAY: "Mercredi",
  THURSDAY: "Jeudi",
  FRIDAY: "Vendredi",
  SATURDAY: "Samedi",
  SUNDAY: "Dimanche"
};

const navItems = [
  { id: "home", label: "Accueil", helper: "Priorites du jour", roles: ["PRESIDENT", "SECRETAIRE", "ENSEIGNANT", "ELEVE"] },
  { id: "student", label: "Mon espace", helper: "Compte et suivi", roles: ["PRESIDENT", "SECRETAIRE", "ENSEIGNANT", "ELEVE"] },
  { id: "members", label: "Membres", helper: "Dossiers et badges", roles: ["PRESIDENT", "SECRETAIRE"] },
  { id: "planning", label: "Planning", helper: "Cours et competitions", roles: ["PRESIDENT", "SECRETAIRE", "ENSEIGNANT", "ELEVE"] },
  { id: "badges", label: "Badgeage", helper: "Presence en cours", roles: ["PRESIDENT", "SECRETAIRE", "ENSEIGNANT", "ELEVE"] },
  { id: "stats", label: "Pilotage", helper: "Indicateurs club", roles: ["PRESIDENT"] }
];

function futureDate(days) {
  const date = new Date();
  date.setDate(date.getDate() + days);
  return date.toISOString().slice(0, 10);
}

function todayJavaDay() {
  return ["SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"][new Date().getDay()];
}

const initialCourse = {
  titre: "Atelier technique",
  niveauCible: 3,
  jour: todayJavaDay(),
  heureDebut: "18:30",
  lieu: "Salle CallMe",
  dureeMinutes: 75,
  dateDebut: futureDate(8)
};

const initialRegister = {
  nom: "",
  prenom: "",
  email: "",
  username: "",
  password: "",
  ville: "Toulouse",
  pays: "France",
  niveauExpertise: 1
};

const initialMember = {
  nom: "",
  prenom: "",
  email: "",
  username: "",
  password: "",
  ville: "Toulouse",
  pays: "France",
  niveauExpertise: 1,
  role: "ELEVE"
};

function loadStoredSession() {
  const saved = localStorage.getItem("odoru-session");
  return saved ? JSON.parse(saved) : null;
}

function formatError(status, body) {
  if (status === 401) return "Connexion requise ou session expiree.";
  if (status === 403) return "Action non autorisee pour ce role.";
  return body?.message || body?.error || `Erreur ${status}`;
}

async function readResponse(response) {
  const text = await response.text();
  if (!text) return null;
  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
}

function App() {
  const [session, setSession] = useState(loadStoredSession);
  const [activeView, setActiveView] = useState("home");
  const [authMode, setAuthMode] = useState("login");
  const [credentials, setCredentials] = useState({ username: "president", password: "pres123" });
  const [register, setRegister] = useState(initialRegister);
  const [memberDraft, setMemberDraft] = useState(initialMember);
  const [course, setCourse] = useState(initialCourse);
  const [competition, setCompetition] = useState({ ...initialCourse, titre: "Rencontre technique" });
  const [memberId, setMemberId] = useState("4");
  const [courseLevel, setCourseLevel] = useState("3");
  const [badgeNumber, setBadgeNumber] = useState("BADGE-SAMI-001");
  const [targetBadge, setTargetBadge] = useState("BADGE-SAMI-001");
  const [etatInscription, setEtatInscription] = useState("ACTIVE");
  const [newLevel, setNewLevel] = useState("3");
  const [competitionId, setCompetitionId] = useState("1");
  const [note, setNote] = useState("15");
  const [focusedMember, setFocusedMember] = useState(null);
  const [loading, setLoading] = useState(false);
  const [notice, setNotice] = useState(() => {
    const storedSession = loadStoredSession();
    if (storedSession) {
      return { type: "success", text: `Session active : ${roleLabels[storedSession.role] || storedSession.role}.` };
    }
    return { type: "info", text: "Connecte-toi ou cree un compte eleve pour commencer." };
  });
  const [data, setData] = useState(null);

  const visibleNav = useMemo(() => {
    if (!session) return [];
    return navItems.filter((item) => item.roles.includes(session.role));
  }, [session]);

  const authHeaders = useMemo(() => {
    if (!session?.token) return {};
    return { Authorization: `Bearer ${session.token}` };
  }, [session]);

  async function request(path, options = {}, successText = "Operation effectuee.", withAuth = true) {
    setLoading(true);
    setNotice({ type: "info", text: "Traitement en cours..." });
    try {
      const response = await fetch(`${API}${path}`, {
        ...options,
        headers: {
          "Content-Type": "application/json",
          ...(withAuth ? authHeaders : {}),
          ...options.headers
        }
      });
      const body = await readResponse(response);
      if (!response.ok) {
        setNotice({ type: "error", text: formatError(response.status, body) });
        setData(body);
        return null;
      }
      setNotice({ type: "success", text: successText });
      setData(body);
      return body;
    } catch (error) {
      setNotice({ type: "error", text: `Backend indisponible sur ${API}.` });
      setData({ message: error.message });
      return null;
    } finally {
      setLoading(false);
    }
  }

  async function login(event) {
    event.preventDefault();
    const body = await request(
      "/api/auth/login",
      { method: "POST", body: JSON.stringify(credentials) },
      "Session ouverte.",
      false
    );
    if (body?.token) {
      localStorage.setItem("odoru-session", JSON.stringify(body));
      setSession(body);
      setActiveView("home");
      setNotice({ type: "success", text: `Connecte en ${roleLabels[body.role] || body.role}.` });
    }
  }

  async function createPublicAccount(event) {
    event.preventDefault();
    const body = {
      ...register,
      niveauExpertise: Number(register.niveauExpertise)
    };
    const created = await request(
      "/api/auth/register",
      { method: "POST", body: JSON.stringify(body) },
      "Compte eleve cree. Tu peux maintenant te connecter.",
      false
    );
    if (created?.username) {
      setCredentials({ username: register.username, password: register.password });
      setAuthMode("login");
    }
  }

  function logout() {
    localStorage.removeItem("odoru-session");
    setSession(null);
    setActiveView("home");
    setData(null);
    setFocusedMember(null);
    setNotice({ type: "info", text: "Session fermee." });
  }

  function updateState(setter, field, value, numericFields = []) {
    setter((current) => ({
      ...current,
      [field]: numericFields.includes(field) ? Number(value) : value
    }));
  }

  function viewTitle() {
    if (!session) return authMode === "login" ? "Connexion" : "Creation de compte";
    return {
      home: "Tableau de bord",
      student: "Mon espace",
      members: "Membres & inscriptions",
      planning: "Planning & competitions",
      badges: "Badgeage",
      stats: "Pilotage"
    }[activeView];
  }

  async function loadManagedMember() {
    const body = await request(`/api/membres/${memberId}`, {}, "Fiche membre chargee.");
    if (body?.username) setFocusedMember(body);
  }

  async function loadManagedMemberState() {
    const body = await request(`/api/membres/${memberId}/inscription`, {}, "Etat charge.");
    if (focusedMember?.id === Number(memberId) && typeof body === "string") {
      setFocusedMember((current) => ({ ...current, etatInscription: body }));
    }
  }

  async function updateManagedLevel() {
    const body = await request(
      `/api/membres/${memberId}/niveau`,
      { method: "PUT", body: JSON.stringify({ niveauExpertise: Number(newLevel) }) },
      "Niveau mis a jour."
    );
    if (body?.username) setFocusedMember(body);
  }

  async function updateManagedState() {
    const body = await request(
      `/api/membres/${memberId}/inscription`,
      { method: "PUT", body: JSON.stringify({ etatInscription }) },
      "Etat d'inscription mis a jour."
    );
    if (body?.username) setFocusedMember(body);
  }

  async function updateManagedBadge() {
    const body = await request(
      `/api/membres/${memberId}/badge`,
      { method: "PUT", body: JSON.stringify({ numeroBadge: targetBadge }) },
      "Badge associe."
    );
    if (body?.username) setFocusedMember(body);
  }

  async function removeManagedBadge() {
    const body = await request(`/api/membres/${memberId}/badge`, { method: "DELETE" }, "Badge dissocie.");
    if (body?.username) setFocusedMember(body);
  }

  if (!session) {
    return (
      <main className="app-shell app-public">
        <section className="access-panel retro-window">
          <div className="retro-window-header">
            <div className="window-dots" aria-hidden="true"><span></span><span></span></div>
            <span>acces-club.odoru</span>
          </div>
          <header className="page-header public-header">
            <div>
              <img className="logo-main" src="/Logo.png" alt="Odoru" />
              <p className="lead">Le poste simple pour connecter les eleves, badger les presences et piloter la vie courante du club.</p>
            </div>
            <div className={`notice ${notice.type}`}>{notice.text}</div>
          </header>
          <div className="access-layout">
            <AuthPanel
              mode={authMode}
              setMode={setAuthMode}
              credentials={credentials}
              setCredentials={setCredentials}
              register={register}
              setRegister={setRegister}
              loading={loading}
              onLogin={login}
              onRegister={createPublicAccount}
            />
            <PublicWorkspace
              badgeNumber={badgeNumber}
              setBadgeNumber={setBadgeNumber}
              onBadge={() =>
                request(
                  "/api/badges/badger",
                  { method: "POST", body: JSON.stringify({ numeroBadge: badgeNumber }) },
                  "Presence enregistree.",
                  false
                )
              }
            />
          </div>
          <ResultPanel data={data} />
        </section>
      </main>
    );
  }

  return (
    <main className="app-shell app-authenticated">
      <aside className="sidebar retro-window">
        <div className="retro-window-header">
          <div className="window-dots" aria-hidden="true"><span></span><span></span></div>
          <span>navigation.odoru</span>
        </div>
        <div className="brand-block">
          <img className="logo-icon" src="/Icone.png" alt="" aria-hidden="true" />
          <div>
            <p className="eyebrow">BackOffice CallMe</p>
            <h1>Odoru</h1>
          </div>
        </div>
        <div className="identity">
          <span>{roleLabels[session.role] || session.role}</span>
          <strong>Membre #{session.membreId}</strong>
        </div>
        <nav className="nav-list" aria-label="Navigation principale">
          {visibleNav.map((item) => (
            <button
              key={item.id}
              className={activeView === item.id ? "active" : ""}
              type="button"
              onClick={() => setActiveView(item.id)}
            >
              <span>{item.label}</span>
              <small>{item.helper}</small>
            </button>
          ))}
        </nav>
        <button type="button" onClick={logout}>Deconnexion</button>
      </aside>

      <section className="content retro-window">
        <div className="retro-window-header">
          <div className="window-dots" aria-hidden="true"><span></span><span></span></div>
          <span>{viewTitle().toLowerCase()}.odoru</span>
        </div>

        <header className="page-header">
          <div>
            <p className="eyebrow">{session ? "Poste de travail" : "Acces club"}</p>
            <h2>{viewTitle()}</h2>
          </div>
          <div className={`notice ${notice.type}`}>{notice.text}</div>
        </header>

        <>
            {activeView === "home" && (
              <HomeWorkspace
                session={session}
                setActiveView={setActiveView}
                onProfile={() => request(`/api/membres/${session.membreId}`, {}, "Profil charge.")}
                onCourses={() => request(`/api/cours?niveauCible=${courseLevel}`, {}, "Cours charges.")}
              />
            )}
            {activeView === "student" && (
              <StudentWorkspace
                session={session}
                memberId={memberId}
                setMemberId={setMemberId}
                onProfile={() => request(`/api/membres/${session.membreId}`, {}, "Compte charge.")}
                onHistory={() => request(`/api/membres/${session.membreId}/historique`, {}, "Historique charge.")}
                onResults={() =>
                  request(`/api/membres/${session.membreId}/competitions/resultats`, {}, "Notes chargees.")
                }
                onOtherHistory={() => request(`/api/membres/${memberId}/historique`, {}, "Historique charge.")}
              />
            )}
            {activeView === "members" && (
              <MembersWorkspace
                memberDraft={memberDraft}
                setMemberDraft={setMemberDraft}
                memberId={memberId}
                setMemberId={setMemberId}
                newLevel={newLevel}
                setNewLevel={setNewLevel}
                etatInscription={etatInscription}
                setEtatInscription={setEtatInscription}
                targetBadge={targetBadge}
                setTargetBadge={setTargetBadge}
                focusedMember={focusedMember}
                onList={() => request("/api/membres", {}, "Membres charges.")}
                onMember={loadManagedMember}
                onCreate={() =>
                  request(
                    "/api/membres",
                    {
                      method: "POST",
                      body: JSON.stringify({
                        ...memberDraft,
                        niveauExpertise: Number(memberDraft.niveauExpertise)
                      })
                    },
                    "Membre cree."
                  )
                }
                onLevel={updateManagedLevel}
                onEtat={updateManagedState}
                onGetEtat={loadManagedMemberState}
                onBadge={updateManagedBadge}
                onUnbadge={removeManagedBadge}
              />
            )}
            {activeView === "planning" && (
              <PlanningWorkspace
                session={session}
                course={course}
                competition={competition}
                courseLevel={courseLevel}
                setCourseLevel={setCourseLevel}
                updateCourse={(field, value) =>
                  updateState(setCourse, field, value, ["niveauCible", "dureeMinutes"])
                }
                updateCompetition={(field, value) =>
                  updateState(setCompetition, field, value, ["niveauCible", "dureeMinutes"])
                }
                competitionId={competitionId}
                setCompetitionId={setCompetitionId}
                memberId={memberId}
                setMemberId={setMemberId}
                note={note}
                setNote={setNote}
                onCoursesByLevel={() => request(`/api/cours?niveauCible=${courseLevel}`, {}, "Cours charges.")}
                onMyCourses={() => request(`/api/cours?enseignantId=${session.membreId}`, {}, "Cours enseignant charges.")}
                onCreateCourse={() =>
                  request("/api/cours", { method: "POST", body: JSON.stringify(course) }, "Cours planifie.")
                }
                onCreateCompetition={() =>
                  request(
                    "/api/competitions",
                    { method: "POST", body: JSON.stringify(competition) },
                    "Competition planifiee."
                  )
                }
                onCompetition={() => request(`/api/competitions/${competitionId}`, {}, "Competition chargee.")}
                onCompetitionResults={() =>
                  request(`/api/competitions/${competitionId}/resultats`, {}, "Resultats competition charges.")
                }
                onSaveResult={() =>
                  request(
                    `/api/competitions/${competitionId}/resultats/${memberId}`,
                    { method: "PUT", body: JSON.stringify({ note: Number(note) }) },
                    "Note enregistree."
                  )
                }
              />
            )}
            {activeView === "badges" && (
              <BadgeWorkspace
                badgeNumber={badgeNumber}
                setBadgeNumber={setBadgeNumber}
                memberId={memberId}
                setMemberId={setMemberId}
                onBadge={() =>
                  request(
                    "/api/badges/badger",
                    { method: "POST", body: JSON.stringify({ numeroBadge: badgeNumber }) },
                    "Presence enregistree.",
                    false
                  )
                }
                onHistory={() => request(`/api/membres/${memberId}/historique`, {}, "Historique charge.")}
              />
            )}
            {activeView === "stats" && (
              <StatsWorkspace
                memberId={memberId}
                setMemberId={setMemberId}
                courseLevel={courseLevel}
                setCourseLevel={setCourseLevel}
                onCourseStats={() => request("/api/stats/cours", {}, "Statistiques de cours chargees.")}
                onCompetitionStats={() =>
                  request(`/api/stats/competitions?niveauCible=${courseLevel}`, {}, "Statistiques competitions chargees.")
                }
                onMemberCourses={() => request(`/api/stats/membres/${memberId}/cours`, {}, "Presence eleve chargee.")}
                onMemberCompetitions={() =>
                  request(`/api/stats/membres/${memberId}/competitions`, {}, "Resultats eleve charges.")
                }
              />
            )}
        </>

        <ResultPanel data={data} />
      </section>
    </main>
  );
}

function AuthPanel({ mode, setMode, credentials, setCredentials, register, setRegister, loading, onLogin, onRegister }) {
  return (
    <section className="auth-shell">
      <div className="mode-switch" role="tablist" aria-label="Acces compte">
        <button className={mode === "login" ? "active" : ""} type="button" onClick={() => setMode("login")}>
          Connexion
        </button>
        <button className={mode === "register" ? "active" : ""} type="button" onClick={() => setMode("register")}>
          Creer compte
        </button>
      </div>

      {mode === "login" ? (
        <form className="login-card" onSubmit={onLogin}>
          <label>Identifiant<input value={credentials.username} onChange={(event) => setCredentials((current) => ({ ...current, username: event.target.value }))} /></label>
          <label>Mot de passe<input type="password" value={credentials.password} onChange={(event) => setCredentials((current) => ({ ...current, password: event.target.value }))} /></label>
          <button className="primary" type="submit" disabled={loading}>Se connecter</button>
          <div className="account-grid">
            {demoAccounts.map((account) => (
              <button key={account.username} type="button" onClick={() => setCredentials(account)}>
                {account.label}
              </button>
            ))}
          </div>
        </form>
      ) : (
        <form className="login-card" onSubmit={onRegister}>
          <div className="row">
            <label>Prenom<input value={register.prenom} onChange={(event) => setRegister((current) => ({ ...current, prenom: event.target.value }))} /></label>
            <label>Nom<input value={register.nom} onChange={(event) => setRegister((current) => ({ ...current, nom: event.target.value }))} /></label>
          </div>
          <label>Email<input type="email" value={register.email} onChange={(event) => setRegister((current) => ({ ...current, email: event.target.value }))} /></label>
          <label>Identifiant<input value={register.username} onChange={(event) => setRegister((current) => ({ ...current, username: event.target.value }))} /></label>
          <label>Mot de passe<input type="password" value={register.password} onChange={(event) => setRegister((current) => ({ ...current, password: event.target.value }))} /></label>
          <div className="row">
            <label>Ville<input value={register.ville} onChange={(event) => setRegister((current) => ({ ...current, ville: event.target.value }))} /></label>
            <label>Pays<input value={register.pays} onChange={(event) => setRegister((current) => ({ ...current, pays: event.target.value }))} /></label>
          </div>
          <label>Niveau<input type="number" min="1" max="5" value={register.niveauExpertise} onChange={(event) => setRegister((current) => ({ ...current, niveauExpertise: Number(event.target.value) }))} /></label>
          <button className="primary" type="submit" disabled={loading}>Creer mon compte</button>
        </form>
      )}
    </section>
  );
}

function PublicWorkspace({ badgeNumber, setBadgeNumber, onBadge }) {
  return (
    <section className="workflow-grid public-actions">
      <article className="work-card retro-card">
        <h3>Borne CallMe</h3>
        <p>Badgeage disponible sans connexion, comme a l'entree du cours. Badge de test : <strong>BADGE-SAMI-001</strong>.</p>
        <label>Numero de badge<input value={badgeNumber} onChange={(event) => setBadgeNumber(event.target.value)} /></label>
        <button className="primary" type="button" onClick={onBadge}>Badger ma presence</button>
      </article>
    </section>
  );
}

function HomeWorkspace({ session, setActiveView, onProfile, onCourses }) {
  return (
    <section className="workflow-grid">
      <article className="work-card retro-card span-2">
        <h3>Parcours principal</h3>
        <p>Choisis un poste de travail dans le menu. Les actions visibles suivent le role connecte et les cas d'utilisation du club.</p>
      </article>
      <TaskCard title="Consulter mon compte" text="Profil, niveau, inscription." action="Ouvrir" onClick={onProfile} />
      <TaskCard title="Voir les cours" text="Planning filtre par niveau courant." action="Consulter" onClick={onCourses} />
      {session.role === "PRESIDENT" && <TaskCard title="Visualiser les stats" text="Indicateurs club et suivi eleve." action="Pilotage" onClick={() => setActiveView("stats")} />}
      {(session.role === "SECRETAIRE" || session.role === "PRESIDENT") && <TaskCard title="Gerer les inscriptions" text="Etat, badges et niveau des membres." action="Secretariat" onClick={() => setActiveView("members")} />}
      {session.role === "ENSEIGNANT" && <TaskCard title="Planifier" text="Cours, competitions et resultats." action="Planning" onClick={() => setActiveView("planning")} />}
    </section>
  );
}

function StudentWorkspace({ session, memberId, setMemberId, onProfile, onHistory, onResults, onOtherHistory }) {
  const canInspectOther = session.role === "PRESIDENT" || session.role === "SECRETAIRE";
  return (
    <section className="workflow-grid">
      <TaskCard title="Consulter mon compte" text="Identite, role, niveau et etat d'inscription." action="Charger" onClick={onProfile} />
      <TaskCard title="Historique cours suivis" text="Presences enregistrees par badgeage." action="Voir" onClick={onHistory} />
      <TaskCard title="Notes competitions" text="Resultats de competition de l'eleve connecte." action="Voir" onClick={onResults} />
      {canInspectOther && (
        <article className="work-card retro-card">
          <h3>Controle historique</h3>
          <label>ID eleve<input value={memberId} onChange={(event) => setMemberId(event.target.value)} /></label>
          <button type="button" onClick={onOtherHistory}>Historique eleve</button>
        </article>
      )}
    </section>
  );
}

function MembersWorkspace(props) {
  const {
    memberDraft, setMemberDraft, memberId, setMemberId, newLevel, setNewLevel, etatInscription, setEtatInscription,
    targetBadge, setTargetBadge, focusedMember, onList, onMember, onCreate, onLevel, onEtat, onGetEtat, onBadge, onUnbadge
  } = props;
  return (
    <section className="workflow-grid">
      <article className="member-focus retro-card span-2">
        <div>
          <p className="eyebrow">Dossier actif</p>
          <h3>{focusedMember ? `${focusedMember.prenom} ${focusedMember.nom}` : `Membre #${memberId}`}</h3>
        </div>
        <DataGrid entries={[
          ["id", focusedMember?.id || memberId],
          ["role", focusedMember ? (roleLabels[focusedMember.role] || focusedMember.role) : "A charger"],
          ["niveauExpertise", focusedMember?.niveauExpertise || newLevel],
          ["etatInscription", focusedMember?.etatInscription || etatInscription]
        ]} />
      </article>
      <TaskCard title="Repertoire membres" text="Afficher la liste, puis copier l'ID du membre a traiter." action="Afficher" onClick={onList} />
      <article className="work-card retro-card">
        <h3>Choisir un membre</h3>
        <label>ID membre<input value={memberId} onChange={(event) => setMemberId(event.target.value)} /></label>
        <div className="button-row">
          <button className="primary" type="button" onClick={onMember}>Charger dossier</button>
          <button type="button" onClick={onGetEtat}>Verifier etat</button>
        </div>
      </article>
      <article className="work-card retro-card span-2">
        <h3>Creer un compte interne</h3>
        <div className="row">
          <label>Prenom<input value={memberDraft.prenom} onChange={(event) => setMemberDraft((current) => ({ ...current, prenom: event.target.value }))} /></label>
          <label>Nom<input value={memberDraft.nom} onChange={(event) => setMemberDraft((current) => ({ ...current, nom: event.target.value }))} /></label>
        </div>
        <div className="row">
          <label>Email<input value={memberDraft.email} onChange={(event) => setMemberDraft((current) => ({ ...current, email: event.target.value }))} /></label>
          <label>Identifiant<input value={memberDraft.username} onChange={(event) => setMemberDraft((current) => ({ ...current, username: event.target.value }))} /></label>
        </div>
        <div className="row">
          <label>Mot de passe<input type="password" value={memberDraft.password} onChange={(event) => setMemberDraft((current) => ({ ...current, password: event.target.value }))} /></label>
          <label>Role<select value={memberDraft.role} onChange={(event) => setMemberDraft((current) => ({ ...current, role: event.target.value }))}>
            <option value="ELEVE">Eleve</option>
            <option value="ENSEIGNANT">Enseignant</option>
            <option value="SECRETAIRE">Secretaire</option>
            <option value="PRESIDENT">President</option>
          </select></label>
        </div>
        <div className="row">
          <label>Ville<input value={memberDraft.ville} onChange={(event) => setMemberDraft((current) => ({ ...current, ville: event.target.value }))} /></label>
          <label>Niveau<input type="number" min="1" max="5" value={memberDraft.niveauExpertise} onChange={(event) => setMemberDraft((current) => ({ ...current, niveauExpertise: Number(event.target.value) }))} /></label>
        </div>
        <button className="primary" type="button" onClick={onCreate}>Creer le membre</button>
      </article>
      <article className="work-card retro-card">
        <h3>Inscription & niveau</h3>
        <p>Ces actions s'appliquent au dossier actif : <strong>{focusedMember ? `${focusedMember.prenom} ${focusedMember.nom}` : `membre #${memberId}`}</strong>.</p>
        <label>Niveau<input value={newLevel} onChange={(event) => setNewLevel(event.target.value)} /></label>
        <button type="button" onClick={onLevel}>MAJ niveau</button>
        <label>Etat<select value={etatInscription} onChange={(event) => setEtatInscription(event.target.value)}>
          <option value="ACTIVE">ACTIVE</option>
          <option value="EN_ATTENTE">EN_ATTENTE</option>
          <option value="SUSPENDUE">SUSPENDUE</option>
          <option value="EXPIREE">EXPIREE</option>
        </select></label>
        <button type="button" onClick={onEtat}>MAJ inscription</button>
      </article>
      <article className="work-card retro-card">
        <h3>Badge membre</h3>
        <p>Association badge pour <strong>{focusedMember ? `${focusedMember.prenom} ${focusedMember.nom}` : `membre #${memberId}`}</strong>.</p>
        <label>Numero badge<input value={targetBadge} onChange={(event) => setTargetBadge(event.target.value)} /></label>
        <div className="button-row">
          <button type="button" onClick={onBadge}>Associer</button>
          <button type="button" onClick={onUnbadge}>Dissocier</button>
        </div>
      </article>
    </section>
  );
}

function PlanningWorkspace(props) {
  const {
    session, course, competition, courseLevel, setCourseLevel, updateCourse, updateCompetition,
    competitionId, setCompetitionId, memberId, setMemberId, note, setNote, onCoursesByLevel, onMyCourses,
    onCreateCourse, onCreateCompetition, onCompetition, onCompetitionResults, onSaveResult
  } = props;
  const canPlan = session.role === "ENSEIGNANT";
  const canResult = session.role === "ENSEIGNANT" || session.role === "PRESIDENT";
  return (
    <section className="workflow-grid">
      <article className="work-card retro-card">
        <h3>Consulter planning</h3>
        <label>Niveau<input value={courseLevel} onChange={(event) => setCourseLevel(event.target.value)} /></label>
        <div className="button-row">
          <button type="button" onClick={onCoursesByLevel}>Cours niveau</button>
          <button type="button" onClick={onMyCourses}>Mes cours</button>
        </div>
      </article>
      {canPlan && <EventForm title="Planifier cours" event={course} update={updateCourse} onSubmit={onCreateCourse} action="Creer cours" />}
      {canPlan && <EventForm title="Planifier competition" event={competition} update={updateCompetition} onSubmit={onCreateCompetition} action="Creer competition" />}
      {canResult && (
        <article className="work-card retro-card">
          <h3>Resultats competition</h3>
          <label>ID competition<input value={competitionId} onChange={(event) => setCompetitionId(event.target.value)} /></label>
          <label>ID eleve<input value={memberId} onChange={(event) => setMemberId(event.target.value)} /></label>
          <label>Note<input value={note} onChange={(event) => setNote(event.target.value)} /></label>
          <div className="button-row">
            <button type="button" onClick={onCompetition}>Consulter</button>
            <button type="button" onClick={onCompetitionResults}>Resultats</button>
            <button className="primary" type="button" onClick={onSaveResult}>Enregistrer note</button>
          </div>
        </article>
      )}
    </section>
  );
}

function EventForm({ title, event, update, onSubmit, action }) {
  return (
    <article className="work-card retro-card span-2">
      <h3>{title}</h3>
      <label>Titre<input value={event.titre} onChange={(e) => update("titre", e.target.value)} /></label>
      <div className="row">
        <label>Niveau<input type="number" min="1" max="5" value={event.niveauCible} onChange={(e) => update("niveauCible", e.target.value)} /></label>
        <label>Jour<select value={event.jour} onChange={(e) => update("jour", e.target.value)}>
          {Object.entries(dayLabels).map(([value, label]) => <option key={value} value={value}>{label}</option>)}
        </select></label>
      </div>
      <div className="row">
        <label>Heure<input type="time" value={event.heureDebut} onChange={(e) => update("heureDebut", e.target.value)} /></label>
        <label>Duree<input type="number" min="15" step="15" value={event.dureeMinutes} onChange={(e) => update("dureeMinutes", e.target.value)} /></label>
      </div>
      <div className="row">
        <label>Lieu<input value={event.lieu} onChange={(e) => update("lieu", e.target.value)} /></label>
        <label>Date<input type="date" value={event.dateDebut} onChange={(e) => update("dateDebut", e.target.value)} /></label>
      </div>
      <button className="primary" type="button" onClick={onSubmit}>{action}</button>
    </article>
  );
}

function BadgeWorkspace({ badgeNumber, setBadgeNumber, memberId, setMemberId, onBadge, onHistory }) {
  return (
    <section className="workflow-grid">
      <article className="work-card retro-card badge-card">
        <h3>Borne de badgeage</h3>
        <p>Badge de test associe a Sami Petit : <strong>BADGE-SAMI-001</strong>.</p>
        <label>Numero de badge<input value={badgeNumber} onChange={(event) => setBadgeNumber(event.target.value)} /></label>
        <button className="primary" type="button" onClick={onBadge}>Enregistrer la presence</button>
      </article>
      <article className="work-card retro-card">
        <h3>Verification historique</h3>
        <label>ID eleve<input value={memberId} onChange={(event) => setMemberId(event.target.value)} /></label>
        <button type="button" onClick={onHistory}>Voir l'historique</button>
      </article>
    </section>
  );
}

function StatsWorkspace({ memberId, setMemberId, courseLevel, setCourseLevel, onCourseStats, onCompetitionStats, onMemberCourses, onMemberCompetitions }) {
  return (
    <section className="workflow-grid">
      <TaskCard title="Stats cours" text="Volume de cours et moyenne de presents." action="Calculer" onClick={onCourseStats} />
      <article className="work-card retro-card">
        <h3>Stats competitions</h3>
        <label>Niveau<input value={courseLevel} onChange={(event) => setCourseLevel(event.target.value)} /></label>
        <button type="button" onClick={onCompetitionStats}>Visualiser</button>
      </article>
      <article className="work-card retro-card">
        <h3>Suivi eleve</h3>
        <label>ID eleve<input value={memberId} onChange={(event) => setMemberId(event.target.value)} /></label>
        <div className="button-row">
          <button type="button" onClick={onMemberCourses}>Cours</button>
          <button type="button" onClick={onMemberCompetitions}>Competitions</button>
        </div>
      </article>
    </section>
  );
}

function TaskCard({ title, text, action, onClick }) {
  return (
    <article className="work-card retro-card">
      <h3>{title}</h3>
      <p>{text}</p>
      <button type="button" onClick={onClick}>{action}</button>
    </article>
  );
}

function ResultPanel({ data }) {
  return (
    <section className="result-panel retro-panel">
      <div className="section-title">
        <h3>Retour operation</h3>
        <span>{data ? "Derniere action" : "En attente"}</span>
      </div>
      <ResultContent data={data} />
    </section>
  );
}

function ResultContent({ data }) {
  if (!data) return <p className="muted">Selectionne une action : son resultat apparaitra ici.</p>;
  if (Array.isArray(data) && data.length === 0) return <p className="muted">Aucun element trouve.</p>;
  if (Array.isArray(data) && data[0]?.username) return <MemberTable members={data} />;
  if (Array.isArray(data) && data[0]?.titre) return <CourseTable courses={data} />;
  if (Array.isArray(data)) return <GenericTable rows={data} />;
  if (data.token) return <SessionCard session={data} />;
  if (data.username) return <ProfileCard member={data} />;
  if (data.coursTitre || data.dateHeure) return <PresenceCard presence={data} />;
  if (data.nombreCours !== undefined) return <StatsCard stats={data} />;
  return <GenericCard data={data} />;
}

function labelize(key) {
  const labels = {
    id: "ID",
    nom: "Nom",
    prenom: "Prenom",
    email: "Email",
    username: "Identifiant",
    role: "Role",
    niveauExpertise: "Niveau",
    etatInscription: "Etat inscription",
    numeroBadge: "Numero badge",
    message: "Message",
    error: "Erreur",
    coursTitre: "Cours",
    membreId: "Membre",
    coursId: "Cours ID",
    dateHeure: "Date et heure",
    moyenneElevesPresents: "Moyenne presents",
    nombreCours: "Nombre de cours"
  };
  if (labels[key]) return labels[key];
  return key.replace(/([A-Z])/g, " $1").replace(/^./, (letter) => letter.toUpperCase());
}

function formatValue(value) {
  if (value === null || value === undefined || value === "") return "Non renseigne";
  if (typeof value === "boolean") return value ? "Oui" : "Non";
  if (typeof value === "object") return JSON.stringify(value);
  return String(value);
}

function DataGrid({ entries }) {
  return (
    <dl className="data-grid">
      {entries.map(([key, value]) => (
        <div className="data-item" key={key}>
          <dt>{labelize(key)}</dt>
          <dd>{formatValue(value)}</dd>
        </div>
      ))}
    </dl>
  );
}

function GenericCard({ data }) {
  if (typeof data === "string") {
    return <div className="detail-list"><strong>Message</strong><span>{data}</span></div>;
  }
  return <DataGrid entries={Object.entries(data)} />;
}

function GenericTable({ rows }) {
  if (!rows.length) return <p className="muted">Aucun element trouve.</p>;
  if (typeof rows[0] !== "object" || rows[0] === null) {
    return <div className="detail-list">{rows.map((value, index) => <span key={index}>{formatValue(value)}</span>)}</div>;
  }
  const columns = Array.from(new Set(rows.flatMap((row) => Object.keys(row)))).slice(0, 7);
  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>{columns.map((column) => <th key={column}>{labelize(column)}</th>)}</tr>
        </thead>
        <tbody>
          {rows.map((row, index) => (
            <tr key={row.id || index}>
              {columns.map((column) => <td key={column}>{formatValue(row[column])}</td>)}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function SessionCard({ session }) {
  return (
    <div className="detail-list success-block">
      <strong>Session ouverte</strong>
      <DataGrid entries={[["role", roleLabels[session.role] || session.role], ["membreId", `#${session.membreId}`]]} />
    </div>
  );
}

function ProfileCard({ member }) {
  return (
    <div className="detail-list">
      <strong>{member.prenom} {member.nom}</strong>
      <DataGrid entries={[
        ["role", roleLabels[member.role] || member.role],
        ["niveauExpertise", member.niveauExpertise],
        ["email", member.email],
        ["etatInscription", member.etatInscription]
      ]} />
    </div>
  );
}

function PresenceCard({ presence }) {
  return (
    <div className="detail-list success-block">
      <strong>Presence enregistree</strong>
      <DataGrid entries={[
        ["coursTitre", presence.coursTitre],
        ["membreId", `#${presence.membreId}`],
        ["coursId", `#${presence.coursId}`],
        ["dateHeure", presence.dateHeure]
      ]} />
    </div>
  );
}

function StatsCard({ stats }) {
  return (
    <div className="detail-list">
      <strong>Statistiques cours</strong>
      <DataGrid entries={[
        ["nombreCours", stats.nombreCours],
        ["moyenneElevesPresents", stats.moyenneElevesPresents]
      ]} />
      {Array.isArray(stats.details) && stats.details.length > 0 && <GenericTable rows={stats.details} />}
    </div>
  );
}

function MemberTable({ members }) {
  return (
    <div className="table-wrap">
      <table>
        <thead><tr><th>ID</th><th>Nom</th><th>Role</th><th>Niveau</th><th>Etat</th></tr></thead>
        <tbody>
          {members.map((member) => (
            <tr key={member.id}>
              <td>{member.id}</td>
              <td>{member.prenom} {member.nom}</td>
              <td>{member.role}</td>
              <td>{member.niveauExpertise}</td>
              <td>{member.etatInscription}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function CourseTable({ courses }) {
  return (
    <div className="table-wrap">
      <table>
        <thead><tr><th>ID</th><th>Cours</th><th>Jour</th><th>Heure</th><th>Lieu</th><th>Prof</th></tr></thead>
        <tbody>
          {courses.map((course) => (
            <tr key={course.id}>
              <td>{course.id}</td>
              <td>{course.titre}</td>
              <td>{dayLabels[course.jour] || course.jour}</td>
              <td>{course.heureDebut}</td>
              <td>{course.lieu}</td>
              <td>{course.enseignantNom}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default App;
