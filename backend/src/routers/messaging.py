from fastapi import APIRouter, Depends, HTTPException, Query
from ..utils.supabase_client import get_supabase_client
from uuid import uuid4

router = APIRouter(prefix="/messaging", tags=["messaging"])

@router.get("/conversations")
def list_conversations():
    supabase = get_supabase_client()
    # user_id = current_user["id"] if isinstance(current_user, dict) else current_user.id
    # Get conversations where user is patient or doctor
    # You may need to adjust this logic if you want to filter by user
    resp = supabase.table("conversations").select("*").execute()
    return resp.data

@router.post("/conversations")
def create_conversation(patient_id: str, doctor_id: str):
    supabase = get_supabase_client()
    # Check if conversation exists
    resp = supabase.table("conversations").select("*") \
        .eq("patient_id", patient_id).eq("doctor_id", doctor_id).single().execute()
    if resp.data:
        return resp.data
    # Create new conversation
    new_conv = {
        "id": str(uuid4()),
        "patient_id": patient_id,
        "doctor_id": doctor_id
    }
    supabase.table("conversations").insert(new_conv).execute()
    return new_conv

@router.get("/messages")
def list_messages(conversation_id: str = Query(...)):
    supabase = get_supabase_client()
    # Check user is part of conversation (removed user check)
    conv = supabase.table("conversations").select("*").eq("id", conversation_id).single().execute().data
    # user_id = current_user["id"] if isinstance(current_user, dict) else current_user.id
    # if not conv or (user_id not in [conv["patient_id"], conv["doctor_id"]]):
    #     raise HTTPException(status_code=403, detail="Not authorized")
    resp = supabase.table("messages").select("*").eq("conversation_id", conversation_id).order("sent_at").execute()
    return resp.data

@router.post("/messages")
def send_message(conversation_id: str, content: str):
    supabase = get_supabase_client()
    conv = supabase.table("conversations").select("*").eq("id", conversation_id).single().execute().data
    # user_id = current_user["id"] if isinstance(current_user, dict) else current_user.id
    # if not conv or (user_id not in [conv["patient_id"], conv["doctor_id"]]):
    #     raise HTTPException(status_code=403, detail="Not authorized")
    msg = {
        "id": str(uuid4()),
        "conversation_id": conversation_id,
        # "sender_id": user_id,  # removed sender/receiver logic
        # "receiver_id": conv["doctor_id"] if user_id == conv["patient_id"] else conv["patient_id"],
        "content": content
    }
    supabase.table("messages").insert(msg).execute()
    return msg 