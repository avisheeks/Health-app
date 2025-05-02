from supabase import create_client
import os
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

# Initialize Supabase client
supabase_url = os.getenv("SUPABASE_URL")
supabase_key = os.getenv("SUPABASE_KEY")

print(f"Supabase URL: {supabase_url}")
print(f"Supabase Key exists: {bool(supabase_key)}")

try:
    # Create Supabase client
    supabase = create_client(supabase_url, supabase_key)
    
    # Try to fetch some data
    response = supabase.table('users').select("*").execute()
    
    print("\nConnection successful!")
    print(f"Number of users found: {len(response.data)}")
    print("\nFirst user data (if any):")
    if response.data:
        print(response.data[0])
        
except Exception as e:
    print("\nError connecting to Supabase:")
    print(str(e)) 