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
function photoIsSafe(url) { if (/^\/api\/plant-photos\/[a-f0-9-]{36}\.(jpg|png)$/i.test(url)) return true; try { const u = new URL(url); return ['https:', 'http:'].includes(u.protocol); } catch { return false; } }
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
  const selector=$('#planningPlant');
  const selected=selector.value;
  selector.replaceChildren(...plants.map(p=>{let o=document.createElement('option');o.value=p.id;o.textContent=p.nickname;return o;}));
  if(plants.some(p=>String(p.id)===selected))selector.value=selected;
  if(selector.value)loadSettings().catch(e=>notify(e.message));
}
async function load() { try { [plants,tasks] = await Promise.all([api('/plants'),api('/tasks')]); render(); await loadNotifications(); } catch(e) { notify('Could not load data: '+e.message); } }
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

// Student 2: live Strategy selection and Observer-generated events.
async function loadSettings(){
  const id=$('#planningPlant').value;if(!id)return;
  const settings=await api(`/plants/${id}/care-settings`);
  $('#careMode').value=settings.mode;
}
async function loadNotifications(){
  const entries=await api('/notifications');const list=$('#notificationList');
  list.replaceChildren(...entries.map(n=>{
    const row=text('div','','task');
    const plant=plants.find(p=>p.id===n.plantId);
    const info=text('div','','info');info.append(text('b',n.message),text('small',`${plant?.nickname??'Plant'} · ${new Date(n.createdAt).toLocaleString()}`));
    row.append(info);
    if(!n.read){const btn=text('button','Mark read','secondary');btn.onclick=async()=>{try{await api(`/notifications/${n.id}/read`,{method:'PATCH'});await loadNotifications();}catch(e){notify(e.message);}};row.append(btn);}
    return row;
  }));
  if(!entries.length)list.append(text('div','No notifications yet','empty'));
}
$('#planningPlant').onchange=()=>loadSettings().catch(e=>notify(e.message));
$('#applyMode').onclick=async()=>{
  const id=$('#planningPlant').value;if(!id)return notify('Add a plant first');
  try{
    await api(`/plants/${id}/care-settings`,{method:'PUT',body:JSON.stringify({mode:$('#careMode').value})});
    $('#planningStatus').textContent='Schedule recalculated. New event notification created.';
    await load();await loadNotifications();notify('Care strategy applied');
  }catch(e){notify(e.message);}
};
$('#refreshNotifications').onclick=()=>loadNotifications().catch(e=>notify(e.message));
loadNotifications().catch(e=>notify(e.message));

// Student 3: Multipart upload -> Spring controller -> GeminiPlantAdapter -> suggested Plant data.
$('#scanBtn').onclick=async()=>{
 const file=$('#plantPhoto').files[0], output=$('#scanOutput');
 if(!file)return notify('Choose a JPG or PNG photo');
 if(file.size>5*1024*1024)return notify('Photo exceeds 5 MB');
 if(!['image/jpeg','image/png'].includes(file.type))return notify('Only JPG/PNG supported');
 $('#scanBtn').disabled=true;output.textContent='Analyzing photo with Gemini…';
 try{
  const body=new FormData();body.append('photo',file);
  const res=await fetch('/api/plants/analyze',{method:'POST',body});
  const data=await res.json().catch(()=>({}));
  if(!res.ok)throw new Error(data.error||data.detail||data.message||`AI request failed (${res.status})`);
  const a=data.analysis;
  output.replaceChildren();
  const description=text('div',`${a.commonName} · ${a.scientificName} · ${a.identificationStatus}`,'ai-result');
  const notes=text('p',a.careSummary||'No additional notes');
  const action=text('button','Review and add plant','primary');
  action.onclick=()=>{
   openEditor();form.elements.nickname.value=a.commonName;
   form.elements.species.value=a.scientificName;
   form.elements.lightLevel.value=a.lightLevel;
   form.elements.wateringIntervalDays.value=a.soilCheckDays;
   form.elements.notes.value=a.careSummary;
   form.elements.photoUrl.value=data.photoUrl;
   notify('Check the AI suggestion and fill in a room before saving');
  };
  output.append(description,notes,action);
 }catch(error){output.textContent='AI could not analyze this photo: '+error.message+' Manual plant creation is still available.';}
 finally{$('#scanBtn').disabled=false;}
};
