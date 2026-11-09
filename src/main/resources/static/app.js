const $ = (selector) => document.querySelector(selector);
const grid = $('#plantsGrid'), tasksEl = $('#taskList'), form = $('#plantForm'), dialog = $('#editor');
let plants = [], tasks = [], editingId = null;

async function api(path, options = {}) {
  const response = await fetch('/api' + path, { ...options,
    headers: { 'Content-Type': 'application/json', ...options.headers } });
  if (response.status === 204) return null;
  const data = await response.json().catch(() => ({}));
  if (!response.ok) throw new Error(data.error || `Request failed (${response.status})`);
  return data;
}
function notify(message) { const el = $('#toast'); el.textContent = message; el.style.display = 'block'; clearTimeout(notify.timer); notify.timer = setTimeout(() => el.style.display='none', 3200); }
function text(tag, value, className) { const element = document.createElement(tag); element.textContent = value ?? ''; if (className) element.className = className; return element; }
function photoIsSafe(url) { try { const u = new URL(url); return ['https:', 'http:'].includes(u.protocol); } catch { return false; } }
function createCard(plant) {
  const card = text('article', '', 'plant-card');
  const visual = text('div', '🌿', 'plant-visual');
  if (plant.photoUrl && photoIsSafe(plant.photoUrl)) {
    const img = document.createElement('img'); img.src = plant.photoUrl; img.alt = plant.nickname; img.onerror = () => { visual.replaceChildren(document.createTextNode('🌿')); }; visual.replaceChildren(img);
  }
  card.append(visual);
  const info = text('div','','plant-info'); info.append(text('h3',plant.nickname),text('p',plant.species + ' · ' + plant.room),text('span',plant.lightLevel.toLowerCase() + ' light','pill'));
  const actions = text('div','','card-actions');
  const edit = text('button','Edit'); edit.onclick = () => openEditor(plant);
  const remove = text('button','Delete','danger'); remove.onclick = () => deletePlant(plant.id);
  actions.append(edit,remove);info.append(actions);card.append(info);return card;
}
function render() {
  const query = $('#search').value.trim().toLowerCase();
  const filtered = plants.filter(p => [p.nickname,p.species,p.room].some(v => v.toLowerCase().includes(query)));
  grid.replaceChildren(...filtered.map(createCard));
  if (!filtered.length) grid.append(text('div','No plants found. Add one to get started!','empty'));
  $('#count').textContent = plants.length;
  tasksEl.replaceChildren(...tasks.map(t => {
    const item = text('div','','task' + (t.completed ? ' done' : ''));
    const checkbox = document.createElement('input'); checkbox.type='checkbox'; checkbox.checked = t.completed; checkbox.setAttribute('aria-label',`Complete ${t.title}`);
    checkbox.onchange = async () => { try { await api(`/tasks/${t.id}/completion`,{method:'PATCH',body:JSON.stringify({completed:checkbox.checked})}); await load(); notify('Care task updated'); } catch(e) { checkbox.checked=!checkbox.checked; notify(e.message); } };
    const plant = plants.find(p => p.id === t.plantId);
    const info = text('div','','info'); info.append(text('b',t.title),text('small',plant ? plant.nickname : 'Plant'));
    item.append(checkbox,info,text('span',t.dueDate,'date')); return item;
  }));
  if (!tasks.length) tasksEl.append(text('div','No care tasks yet.','empty'));
}
async function load() { try { [plants,tasks] = await Promise.all([api('/plants'),api('/tasks')]); render(); } catch(e) { notify('Could not load data: '+e.message); } }
function openEditor(plant = null) {
  editingId = plant?.id ?? null; form.reset(); $('#formError').textContent='';
  $('#formTitle').textContent = plant ? 'Edit plant' : 'Add a new plant';
  if (plant) for (const key of ['nickname','species','room','lightLevel','wateringIntervalDays','notes','photoUrl']) form.elements[key].value=plant[key] ?? '';
  dialog.showModal();
}
async function deletePlant(id) {
  if (!confirm('Delete this plant and its care tasks?')) return;
  try { await api(`/plants/${id}`, {method:'DELETE'}); await load(); notify('Plant deleted'); } catch(e) { notify(e.message); }
}
form.addEventListener('submit', async event => {
  event.preventDefault(); $('#formError').textContent='';
  const values = Object.fromEntries(new FormData(form).entries()); values.wateringIntervalDays=Number(values.wateringIntervalDays);
  if (values.photoUrl && !photoIsSafe(values.photoUrl)) { $('#formError').textContent='Photo URL must start with https:// or http://'; return; }
  $('#saveBtn').disabled=true;
  try { await api(editingId ? `/plants/${editingId}` : '/plants',{method:editingId?'PUT':'POST',body:JSON.stringify(values)}); dialog.close();await load();notify(editingId?'Plant updated':'Plant added with care tasks'); }
  catch(e) { $('#formError').textContent=e.message; }
  finally { $('#saveBtn').disabled=false; }
});
$('#addBtn').onclick=()=>openEditor(); $('#closeBtn').onclick=()=>dialog.close();$('#cancelBtn').onclick=()=>dialog.close();$('#search').oninput=render;
load();
